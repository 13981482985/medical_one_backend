package com.cqupt.software_1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software_1.entity.User;
import com.cqupt.software_1.service.UserService;
import com.cqupt.software_1.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author hp
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-05-16 16:44:39
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getAll() {
        List<User> users = userMapper.selectList(null);
        return users;
    }

    @Override
    public User getUserByName(String userName) {
        User user = userMapper.getUerByUserName(userName);
        return user;
    }

    @Override
    public User getUserById(Integer id) {
        return userMapper.getUserById(id);
    }

    @Override
    public void saveUser(User user) {
        userMapper.saveUser(user);
    }
}




