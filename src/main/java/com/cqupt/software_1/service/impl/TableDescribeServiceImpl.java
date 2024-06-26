package com.cqupt.software_1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software_1.entity.TableDescribeEntity;
import com.cqupt.software_1.mapper.TableDescribeMapper;
import com.cqupt.software_1.service.TableDescribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// TODO 公共模块新增类

@Service
public class TableDescribeServiceImpl extends ServiceImpl<TableDescribeMapper, TableDescribeEntity> implements TableDescribeService {
    @Autowired
    TableDescribeMapper tableDescribeMapper;
    @Override
    public Integer getColCount(String tableName) {
        return tableDescribeMapper.getColCount(tableName);
    }

    @Override
    public Integer getRowCount(String tableName) {
        return tableDescribeMapper.getRowCount(tableName);
    }
}
