package com.pine.template.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pine.template.model.entity.User;
import com.pine.template.service.UserService;
import com.pine.template.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author wangzhihao
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-02-02 15:36:26
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




