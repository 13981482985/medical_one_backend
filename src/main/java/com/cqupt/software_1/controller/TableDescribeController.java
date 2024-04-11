package com.cqupt.software_1.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.software_1.common.R;
import com.cqupt.software_1.entity.TableDescribeEntity;
import com.cqupt.software_1.service.TableDescribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO 公共模块新增类

@RestController
@RequestMapping("/api")
public class TableDescribeController {
    @Autowired
    TableDescribeService tableDescribeService;
    @GetMapping("/tableDescribe")
    public R<TableDescribeEntity> getTableDescribe(@RequestParam("id") String id){ // 参数表的Id
        TableDescribeEntity tableDescribeEntity = tableDescribeService.getOne(new QueryWrapper<TableDescribeEntity>().eq("table_id", id));
        // 根据表名获取字段数和行数
        Integer colCount = tableDescribeService.getColCount(tableDescribeEntity.getTableName());
        Integer rowCount = tableDescribeService.getRowCount(tableDescribeEntity.getTableName());
        tableDescribeEntity.setRowNumber(rowCount);
        tableDescribeEntity.setColNumber(colCount);
        System.out.println("数据为："+ JSON.toJSONString(tableDescribeEntity));
        return R.success("200",tableDescribeEntity);
    }
}
