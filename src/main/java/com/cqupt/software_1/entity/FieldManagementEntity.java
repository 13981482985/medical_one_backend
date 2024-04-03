package com.cqupt.software_1.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO 公共模块新增类

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("field_management")
public class FieldManagementEntity {
    @TableId
    private Integer characterId;
    private String featureName;
    private String chName;
    private Boolean diseaseStandard;

    private Boolean isDemography;
    private Boolean isSociology;
    private Boolean isPhysiological;

    private String tableName;
    private String unit;
    private Boolean isLabel;
    private Boolean discrete;
    private String range;
    private String disease;
}
