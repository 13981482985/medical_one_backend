package com.cqupt.software_1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.software_1.entity.TableDescribeEntity;


// TODO 公共模块新增类

public interface TableDescribeMapper extends BaseMapper<TableDescribeEntity> {
    Integer getColCount(String tableName);

    Integer getRowCount(String tableName);
}
