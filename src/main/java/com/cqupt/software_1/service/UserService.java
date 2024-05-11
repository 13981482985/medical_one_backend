package com.cqupt.software_1.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqupt.software_1.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software_1.vo.InsertUserVo;
import com.cqupt.software_1.vo.UserPwd;

import java.util.List;
import java.util.Map;

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

    Page<User> getAllUserInfo(int pageNum  , int pageSize);

    boolean updateStatusById(Integer uid, Integer role ,double uploadSize, String status);

    boolean removeUserById(Integer uid);

    boolean insertUser(InsertUserVo user);

    Map<String, Object> getUserPage(int pageNum, int pageSize);

    boolean updatePwd(UserPwd user);

    List<User> querUser();


    //    下面方法是管理员端-数据管理新增
    void addTableSize(String uid, float tableSize);
    void minusTableSize(String uid, float tableSize);

    User selectByUid(String uid);
}
