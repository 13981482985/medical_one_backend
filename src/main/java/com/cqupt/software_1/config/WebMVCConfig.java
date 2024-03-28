package com.cqupt.software_1.config;

import com.cqupt.software_1.intercepter.UserLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    @Autowired
    UserLoginInterceptor userLoginInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userLoginInterceptor).addPathPatterns("/**").excludePathPatterns("/user/login").excludePathPatterns("/user/signUp").excludePathPatterns("/user/logout");
    }
}
