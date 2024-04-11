package com.cqupt.software_1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software_1.entity.TableDescribeEntity;


// TODO 公共模块新增类
public interface TableDescribeService extends IService<TableDescribeEntity> {
    Integer getColCount(String tableName);

    Integer getRowCount(String tableName);
}
