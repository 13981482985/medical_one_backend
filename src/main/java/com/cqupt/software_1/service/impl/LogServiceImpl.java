package com.cqupt.software_1.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cqupt.software_1.entity.LogEntity;
import com.cqupt.software_1.entity.User;
import com.cqupt.software_1.entity.UserLog;
import com.cqupt.software_1.mapper.LogMapper;
import com.cqupt.software_1.mapper.UserLogMapper;
import com.cqupt.software_1.mapper.UserMapper;
import com.cqupt.software_1.service.LogService;
import com.jcraft.jsch.JSch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

// TODO 公共模块新增类

@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, LogEntity>
        implements LogService {

    @Autowired
    LogMapper logMapper;
    @Autowired
    UserLogMapper userLogMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    public List<LogEntity> getAllLogs() {
        return logMapper.getAllLogs();
    }

    public void insertLog(String uid, Integer role, String operation) {
        User user = userMapper.selectByUid(Integer.parseInt(uid));

        UserLog logEntity = new UserLog();
        logEntity.setUid(Integer.parseInt(uid));
        logEntity.setUsername(user.getUsername());
        logEntity.setOpType(operation);
        // 创建 DateTimeFormatter 对象，定义日期时间的格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 创建 LocalDateTime 对象，存储当前日期和时间
        LocalDateTime now = LocalDateTime.now();
        // 使用 formatter 格式化 LocalDateTime 对象
        String formattedDate = now.format(formatter);
        logEntity.setOpTime(formattedDate);

        System.out.println("要插入的日志数据为："+JSON.toJSONString(logEntity));

        userLogMapper.insert(logEntity);
    }
}
