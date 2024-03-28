package com.cqupt.software_1.service;

import com.cqupt.software_1.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author hp
* @description 针对表【user】的数据库操作Service
* @createDate 2023-05-16 16:44:39
*/
public interface UserService extends IService<User> {

    List<User> getAll();

    User getUserByName(String userName);

    User getUserById(Integer id);

    void saveUser(User user);
}
