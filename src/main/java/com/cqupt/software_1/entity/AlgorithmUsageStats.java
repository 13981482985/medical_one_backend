package com.cqupt.software_1.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlgorithmUsageStats {
    private String model;  // 算法名
    private int usageCount; // 使用次数
    private String date;

}
