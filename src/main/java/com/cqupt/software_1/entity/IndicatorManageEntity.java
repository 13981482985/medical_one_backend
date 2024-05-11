package com.cqupt.software_1.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndicatorManageEntity {
    private String featureName;
    private String label;
    private String type;
    private String typeCh;
    private Float missRate;
    private Boolean discrete;
    private String missCompleteMethod; // 缺失值补齐算法
    private Integer featureDataType; /** 1：表示数字连续（可以使用所有填充算法） 2：表示数字离散 可以使用中位数，总数，前向填充   3：表示文本类型 只能使用众数和前向填充**/
//    private Boolean cheack = false;
    private Integer rangeSize;
}
