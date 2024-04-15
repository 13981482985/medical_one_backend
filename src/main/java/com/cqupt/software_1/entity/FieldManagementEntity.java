package com.cqupt.software_1.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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

//    @TableField(value = "population")
    private Boolean population;
//    @TableField(value = "society")
    private Boolean society;
//    @TableField(value = "physiology")
    private Boolean physiology;

    private String tableName;
    private String unit;
    private Boolean isLabel;
    private Boolean discrete;
    private String range;
    private String disease;

    private String isClinicaRelationship;
    private String isMultipleDiseases;
    private String isRoomInformation;
    private String isQuestionnaire;
    private String isTimeInformation;
    private Date startTime;
    private Date endTime;
    private String timeSpace;
    private Date createTime;
    private Date updateTime;
    private String tablePeople;
    private String tableOrigin;
    private String type;

}
