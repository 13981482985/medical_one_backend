package com.cqupt.software_1.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO 公共模块新增类

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTableFeatureVo {
    private String button;
    private String chName;
    private Integer characterId;
    private String computeOpt;
    private String featureName;
    private Integer opt;
    private String range;
    private String type;
    private String unit;  // 字段类型
    private String value;
    private String optString;

}
