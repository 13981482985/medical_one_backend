package com.cqupt.software_1.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import java.lang.ref.PhantomReference;
import java.util.Date;

@Data
@TableName(value = "public.notification")
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @TableField(value = "info_id")
    @TableId(type = IdType.AUTO)
    private Integer infoId;
    private Integer uid;
    private String username;
    private Date createTime;
    private String title;
    private String content;
    private Date updateTime;
    private String isDelete;
    private String type;
}
