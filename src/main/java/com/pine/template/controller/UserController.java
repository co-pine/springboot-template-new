package com.pine.template.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.pine.template.common.BaseResponse;
import com.pine.template.common.ResultUtils;
import com.pine.template.model.dto.UserLoginByAccountRequest;
import com.pine.template.model.entity.User;
import com.pine.template.model.vo.UserVO;
import com.pine.template.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户 controller
 *
 * @author pine
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;


    @PostMapping("/login/account")
    public BaseResponse<String> loginByAccount(UserLoginByAccountRequest loginRequest) {
        StpUtil.login(1L);
        return ResultUtils.success("login by account success");
    }

    @PostMapping("/login/mock")
    public BaseResponse<String> login() {
        StpUtil.login(1L);
        return ResultUtils.success("mock login success");
    }

    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser() {
        User user = userService.getOptById(StpUtil.getLoginIdAsLong())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        UserVO userVO = UserVO.builder()
                .build();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    @GetMapping("/logout")
    public BaseResponse<String> logout() {
        StpUtil.logout();
        return ResultUtils.success("退出登录");
    }

}
