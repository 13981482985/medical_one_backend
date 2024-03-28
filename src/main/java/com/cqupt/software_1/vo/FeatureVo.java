package com.cqupt.software_1.vo;


import lombok.Data;

import java.util.List;

// TODO 公共模块新增类

@Data
public class FeatureVo {
    private Integer characterId;
    private String featureName;
    private String chName;
    private String type; // 是否离散
    private List<String> range; // 离散取值 或者数值范围
    private String unit;
}
