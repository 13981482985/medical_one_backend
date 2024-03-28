package com.cqupt.software_1.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO 公共模块新增类

@TableName("table_describe")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TableDescribeEntity {
    @TableId
    private String id;
    private String tableId;
    private String tableName;
    private String createUser;
    private String createTime;
    private String classPath;
}
