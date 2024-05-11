package com.cqupt.software_1.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqupt.software_1.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.software_1.vo.InsertUserVo;
import com.cqupt.software_1.vo.UserPwd;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author hp
* @description 针对表【user】的数据库操作Mapper
* @createDate 2023-05-16 16:44:39
* @Entity com.cqupt.software_1.entity.User
*/

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User getUerByUserName(@Param("userName") String userName);

    User getUserById(Integer id);

    void saveUser(@Param("user") User user);

    IPage<User> getAllUserInfo(Page<?> page);

    boolean updateStatusById(Integer uid,Integer role , double uploadSize, String status);

    void removeUserById(Integer uid);

    void insertUser(InsertUserVo user);

    List<User> selectUserPage(int offset, int pageSize);

    int countUsers();

    void updatePwd(UserPwd user);


    @Update("UPDATE software4.software4user SET upload_size = upload_size-#{size} WHERE uid = #{id}")
    int decUpdateUserColumnById(@Param("id") String id, @Param("size") Double size);

    @Update("UPDATE software4.software4user SET upload_size = upload_size+#{size} WHERE uid = #{id}")
    int recoveryUpdateUserColumnById(@Param("id") String id, @Param("size") Double size);





    //    下面方法是管理员端-数据管理新增
    User selectByUid(Integer uid);
    void addTableSize(Integer uid, float tableSize);
    void minusTableSize(Integer uid, float tableSize);
}




