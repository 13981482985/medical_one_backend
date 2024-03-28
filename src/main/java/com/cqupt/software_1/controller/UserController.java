package com.cqupt.software_1.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.software_1.Util.SecurityUtil;
import com.cqupt.software_1.common.R;
import com.cqupt.software_1.entity.User;
import com.cqupt.software_1.entity.UserLog;
import com.cqupt.software_1.service.UserLogService;
import com.cqupt.software_1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


/**
 *
 * 用户管理模块
 *
 * 用户注册
 * 用户登录
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;


    @Autowired
    private UserLogService userLogService;

    @PostMapping("/signUp")
    public R signUp(@RequestBody User user) throws ParseException {

        // 检查用户名是否已经存在
        user.setUid(0);
        User existUser = userService.getUserByName(user.getUsername());

        if (existUser != null){
            return new R<>(500,"用户已经存在",null);
        }

        String pwd = user.getPassword();

        // 对密码进行加密处理
        String password = SecurityUtil.hashDataSHA256(pwd);
        user.setPassword(password);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        user.setCreateTime(date);
        user.setUpdateTime(null);
        user.setRole(0);
        user.setUid(new Random().nextInt());
        userService.saveUser(user);


        //  操作日志记录

       UserLog userLog = new UserLog();
       User one = userService.getUserByName(user.getUsername());
       Integer uid = one.getUid();
//       userLog.setId(new Random().nextInt());
       userLog.setUid(uid);
       userLog.setOpTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
       userLog.setOpType("用户注册");

       userLogService.save(userLog);

        return new R<>(200,"成功",null);

    }

    @PostMapping("/login")
    public R login(@RequestBody User user, HttpServletResponse response, HttpServletRequest request){

        String userName = user.getUsername();
        User getUser = userService.getUserByName(userName);

        String password = getUser.getPassword();
        if (getUser != null){
            // 进行验证密码
            String pwd = user.getPassword();
            String sha256 = SecurityUtil.hashDataSHA256(pwd);
            if (sha256.equals(password)){
                // 验证成功
                UserLog userLog = new UserLog();
                userLog.setUid(getUser.getUid());
                userLog.setOpTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                userLog.setOpType("登录系统");
                userLog.setUsername(userName);
                System.out.println("userlog:"+userLog);
                userLogService.save(userLog);
                // session认证
                HttpSession session = request.getSession();
                session.setAttribute("username",user.getUsername());
                session.setAttribute("userId",getUser.getUid());

//                String uid = getUser.getUid().toString();
//                Cookie cookie = new Cookie("userId",uid );
//                cookie.setDomain("10.16.80.16");
//                response.addCookie(cookie);

                return new R<>(200,"登录成功",null);
            }else {
                return new R<>(500,"密码错误请重新输入",null);
            }

        }else {
            return new R<>(500,"用户不存在",null);
        }
    }


    @PostMapping("/logout")
    public R logout(HttpServletRequest request){

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        session.invalidate();
        return new R<>(200,"退出成功",null);
    }

}
