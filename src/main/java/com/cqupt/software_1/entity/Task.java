package com.cqupt.software_1.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value ="taskmanage")
public class Task implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String  taskname;
    private String  leader;
    private String  participant;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createtime;

    private String   disease;
    private String   model;
    private String   remark;
    private String   feature;
    private String   result;
    private String   parameters;
    private String   parametersvalues;
    private String   targetcolumn;
    private double  usetime;
    private int     ci;
    private double  ratio;
    private String dataset;

    private Integer userid;
}

