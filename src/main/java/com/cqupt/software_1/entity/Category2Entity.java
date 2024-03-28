package com.cqupt.software_1.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// TODO 公共模块新增类

@TableName("category2")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Category2Entity {
    @TableId
    private String id;
    private Integer catLevel;
    private String label;
    private String parentId;
    private Integer isLeafs;
    private Integer isCommon;
    private String path;
    private Integer isDelete;
    private Integer isWideTable;
    @TableField(exist = false)
    private List<Category2Entity> children;
}
