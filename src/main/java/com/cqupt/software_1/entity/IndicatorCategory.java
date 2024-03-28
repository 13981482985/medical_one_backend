package com.cqupt.software_1.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("indicator_category")
public class IndicatorCategory {
    @TableId
    private Integer id;
    @TableField("indicator_name")
    private String label;
    private Integer level;
    private Integer parentId;
    private String indicator; // 分类指标的英文名称
    @TableField(exist = false)
    private List<IndicatorCategory> children;
}
