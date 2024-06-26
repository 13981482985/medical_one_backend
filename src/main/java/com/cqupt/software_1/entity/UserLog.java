package com.cqupt.software_1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName user_log
 */
@TableName(value ="user_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer uid;

    private String username;

    private String opTime;

    private String opType;

    private static final long serialVersionUID = 1L;
}