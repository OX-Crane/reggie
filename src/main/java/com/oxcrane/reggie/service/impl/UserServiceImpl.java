package com.oxcrane.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oxcrane.reggie.entity.User;
import com.oxcrane.reggie.mapper.UserMapper;
import com.oxcrane.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
