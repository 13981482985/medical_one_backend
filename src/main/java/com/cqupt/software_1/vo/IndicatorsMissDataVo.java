package com.cqupt.software_1.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndicatorsMissDataVo {
    private String index; // 检测指标名称
    private Integer validNumber; // 有效值个数
    private Integer missingQuantity; // 缺失值个数
    private String missCompleteMethod; // 缺失值补齐算法
}
