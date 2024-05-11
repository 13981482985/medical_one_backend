package com.cqupt.software_1.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.cqupt.software_1.entity.LogEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// TODO 公共模块新增类
public interface LogService extends IService<LogEntity> {
    List<LogEntity> getAllLogs();
    void insertLog(String uid, Integer role, String operation);
}
