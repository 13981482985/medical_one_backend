package com.cqupt.software_1.common;


import com.cqupt.software_1.entity.User;

/**
 * @author huing
 * @create 2022-06-24 14:01
 */
public class UserThreadLocal {

    private UserThreadLocal(){}

    private static final ThreadLocal<User> LOCAL = new ThreadLocal<>();

    public static void put(User sysUser){
        LOCAL.set(sysUser);
    }

    public static User get(){
        return LOCAL.get();
    }

    public static void remove(){
        LOCAL.remove();
    }
}
