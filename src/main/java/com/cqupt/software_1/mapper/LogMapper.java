package com.cqupt.software_1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.cqupt.software_1.entity.LogEntity;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.ManagedBean;
import java.util.Date;
import java.util.List;
@Mapper
public interface LogMapper extends BaseMapper<LogEntity> {
    List<LogEntity> getAllLogs();
}
