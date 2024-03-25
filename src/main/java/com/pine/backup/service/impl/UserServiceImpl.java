package com.pine.backup.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pine.backup.common.ErrorCode;
import com.pine.backup.constant.UserConstant;
import com.pine.backup.exception.BusinessException;
import com.pine.backup.manager.SessionManager;
import com.pine.backup.manager.VipConfigManager;
import com.pine.backup.mapper.UserMapper;
import com.pine.backup.model.VipConfig;
import com.pine.backup.model.dto.user.UserQueryRequest;
import com.pine.backup.model.dto.user.ZsxqAuthRequest;
import com.pine.backup.model.entity.User;
import com.pine.backup.model.enums.UserRoleEnum;
import com.pine.backup.model.vo.LoginUserVO;
import com.pine.backup.model.vo.UserVO;
import com.pine.backup.service.UserService;
import com.pine.backup.util.ThreadLocalUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.pine.backup.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户服务实现
 *
* @author pine
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";

    @Resource
    private VipConfigManager vipConfigManager;

    @Resource
    private WxMaService wxMaService;

    @Resource
    private SessionManager sessionManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addUser(User user) {
        // 创建用户
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户注册失败");
        }
        // 生成开发者信息
        return user.getId();
    }

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        sessionManager.login(user);
        return this.getLoginUserVO(user);
    }

    @Override
    public LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request) {
        String unionId = wxOAuth2UserInfo.getUnionId();
        String mpOpenId = wxOAuth2UserInfo.getOpenid();
        // 单机锁
        synchronized (unionId.intern()) {
            // 查询用户是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("unionId", unionId);
            User user = this.getOne(queryWrapper);
            // 被封号，禁止登录
            if (user != null && UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该用户已被封，禁止登录");
            }
            // 用户不存在则创建
            if (user == null) {
                user = new User();
                user.setUnionId(unionId);
                user.setMpOpenId(mpOpenId);
                user.setUserAvatar(wxOAuth2UserInfo.getHeadImgUrl());
                user.setUserName(wxOAuth2UserInfo.getNickname());
                boolean result = this.save(user);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败");
                }
            }
            // 记录用户的登录态
            sessionManager.login(user);
            return getLoginUserVO(user);
        }
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        long userId = StpUtil.getLoginIdAsLong();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        User currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @return
     */
    @Override
    public User getLoginUserPermitNull() {
        // 先判断是否已登录
        User currentUser = null;
        try {
            currentUser = ThreadLocalUtil.getLoginUser();
        } catch (Exception ignored) {
        }
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }

        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    @Override
    public boolean hasVipAuth(User user) {
        if (user == null) {
            return false;
        }
        String userRole = user.getUserRole();
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(userRole);
        Date vipExpireTime = user.getVipExpireTime();
        // 是管理员，有权限
        if (UserRoleEnum.ADMIN.equals(userRoleEnum)) {
            return true;
        }
        // 不是 VIP 或超级 VIP
        if (!UserConstant.VIP_ROLE_ENUM_LIST.contains(userRoleEnum)) {
            return false;
        }
        // VIP 已过期
        if (vipExpireTime == null || vipExpireTime.before(new Date())) {
            return false;
        }
        return true;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        sessionManager.logout(request);
        return true;
    }

    @Override
    public Boolean updateUserVipInfo(User oldUser, Date vipExpireTime, ZsxqAuthRequest zsxqAuthRequest, String vipType) {
        final Long userId = oldUser.getId();
        User user = this.getById(userId);
        // 更新用户信息
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setVipExpireTime(vipExpireTime);
        // 不是管理员，才设置权限为 VIP
        if (!UserRoleEnum.ADMIN.getValue().equals(oldUser.getUserRole())) {
            updateUser.setUserRole(vipType);
        }

        VipConfig config = vipConfigManager.getVipConfigByRole(vipType);
        if (config == null) {
            log.info("用户 {} 会员信息更新失败，会员类型 {}", userId, vipType);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "会员类型异常");
        }
        // updateUser.setCheckLeft(user.getCheckLeft() + config.getCheckCount());
        // updateUser.setGenerateLeft(user.getGenerateLeft() + config.getGenerateCount());
        // updateUser.setImportLeft(user.getImportLeft() + config.getImportCount());
        // updateUser.setModuleOptimizeLeft(user.getModuleOptimizeLeft() + config.getModuleOptimizeCount());

        // 关联星球信息
        // if (zsxqAuthRequest != null) {
        //     Long planetUserId = zsxqAuthRequest.getUserId();
        //     String userName = zsxqAuthRequest.getUserName();
        //     String planetCode = String.valueOf(zsxqAuthRequest.getUserNumber());
        //     Long joinTime = zsxqAuthRequest.getJoinTime();
        //     Long expireTime = zsxqAuthRequest.getExpireTime();
        //     updateUser.setPlanetCode(planetCode);
        //     updateUser.setPlanetUserId(planetUserId);
        //     // 设置星球信息
        //     UserPlanetExtraInfo userPlanetExtraInfo = new UserPlanetExtraInfo();
        //     userPlanetExtraInfo.setUserName(userName);
        //     String joinTimeStr = null;
        //     if (joinTime != null) {
        //         joinTimeStr = DateUtil.formatDateTime(new Date(joinTime * 1000));
        //     }
        //     userPlanetExtraInfo.setJoinTime(joinTimeStr);
        //     if (expireTime != null && expireTime != 0) {
        //         String expireTimeStr = DateUtil.formatDateTime(new Date(expireTime * 1000));
        //         userPlanetExtraInfo.setExpireTime(expireTimeStr);
        //     }
        //     updateUser.setPlanetExtraInfo(JSONUtil.toJsonStr(userPlanetExtraInfo));
        // }

        // 更新用户
        boolean result = this.updateById(updateUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户会员信息失败");
        }
        log.info("updateUserVipInfo updateUser = {}", updateUser);
        return result;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = UserVO.builder().build();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        List<String> ascSortField = userQueryRequest.getAscSortField();
        List<String> descSortField = userQueryRequest.getDescSortField();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);

        // MyBatis-plus 自带 columnToSqlSegment 方法进行注入过滤处理，不需要SqlUtils.validSortField(sortField)
        boolean ascValid = ascSortField != null && ascSortField.size() > 0;
        boolean descValid = descSortField != null && descSortField.size() > 0;
        queryWrapper.orderByAsc(ascValid, ascSortField);
        queryWrapper.orderByDesc(descValid, descSortField);

        return queryWrapper;
    }

    /**
     * 微信小程序登录
     * @param code
     * @param request
     * @return
     */
    @Override
    public LoginUserVO userLoginByWxMiniapp(String code, HttpServletRequest request) {
        WxMaJscode2SessionResult wxMaJscode2SessionResult = null;
        try {
            wxMaJscode2SessionResult = wxMaService.jsCode2SessionInfo(code);
            log.info("获取用户信息成功，unionId: {}", wxMaJscode2SessionResult.getUnionid());
        } catch (WxErrorException e) {
            WxError error = e.getError();
            if (error != null) {
                log.error("小程序登录失败，错误信息: {}， 微信错误码: {}，微信异常信息: {}", e.getMessage(), error.getErrorCode(), error.getErrorMsg());
            } else {
                log.error("小程序登录失败，错误信息: {}", e.getMessage());
            }
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "登录失败");
        }
        String useId;
        String unionId = wxMaJscode2SessionResult.getUnionid();
        if (StrUtil.isBlank(unionId)) {
            // 如果 unionId 为空，使用 openid
            useId = wxMaJscode2SessionResult.getOpenid();
        } else {
            useId = unionId;
        }

        synchronized (useId.intern()) {
            // 查询用户是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            // todo 根据实际情况改
            queryWrapper.eq("wxAppOpenId", useId);
            User oldUser = this.getOne(queryWrapper);
            // 被封号，禁止获取动态码
            if (oldUser != null && UserConstant.BAN_ROLE.equals(oldUser.getUserRole())) {
                log.info("user unionId = {} try login, but ban", useId);
                return null;
            }
            long userId;
            // 用户不存在则创建
            if (oldUser == null) {
                User newUser = getUser(wxMaJscode2SessionResult);
                try {
                    this.addUser(newUser);
                    userId = newUser.getId();
                } catch (Exception e) {
                    log.error("用户注册失败，code: {}, WxMaJscode2SessionResult: {}, newUser: {}", code, wxMaJscode2SessionResult, newUser);
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户注册失败");
                }
            } else {
                userId = oldUser.getId();
            }
            User user = this.getById(userId);
            sessionManager.login(user);
            return this.getLoginUserVO(user);
        }
    }

    private User getUser(WxMaJscode2SessionResult wxMaJscode2SessionResult) {
        String unionId = wxMaJscode2SessionResult.getUnionid();
        String mpOpenId = wxMaJscode2SessionResult.getOpenid();
        User user = new User();
        user.setUnionId(unionId);
        user.setWxAppOpenId(mpOpenId);
        user.setUserName("好记性" + RandomUtil.randomNumbers(4));
        return user;
    }
}
