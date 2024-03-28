package com.cqupt.software_1.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO 公共模块新增类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeatureClassVo {
    private String fieldName;
    private String isDiagnosis;
    private String isExamine;
    private String isPathology;
    private String isVitalSigns;
    private String isLabel;
}
