package com.cqupt.software_1.intercepter;

import com.cqupt.software_1.common.UserThreadLocal;
import com.cqupt.software_1.entity.User;
import com.cqupt.software_1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserLoginInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        for (Cookie cookie : request.getCookies()) {
//            if(cookie.getName().equals("userId")){
//                // 已经登陆
//                User user = userService.getUserById(Integer.parseInt(cookie.getValue()));
//                UserThreadLocal.put(user);
//                return true;
//            }
//        }
        Integer userId = (Integer)request.getSession().getAttribute("userId");
        if(userId!=null){
            User user = userService.getUserById(userId);
            UserThreadLocal.put(user);
            return true;
        }
        System.out.println("没有携带cookie");
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserThreadLocal.remove();
    }
}
