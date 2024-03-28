package com.cqupt.software_1.mapper;

import com.cqupt.software_1.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author hp
* @description 针对表【user】的数据库操作Mapper
* @createDate 2023-05-16 16:44:39
* @Entity com.cqupt.software_1.entity.User
*/
public interface UserMapper extends BaseMapper<User> {

    User getUerByUserName(@Param("userName") String userName);

    User getUserById(Integer id);

    void saveUser(@Param("user") User user);
}




