package com.cqupt.software_1.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@TableName("category")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryEntity {
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
    private List<CategoryEntity> children;

}
