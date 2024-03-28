package com.cqupt.software_1.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;


// 非离散取值
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotDiscrete {
    private Integer total; // 数据总条数
    private Float average; // 平均值
    private Float middle; // 中位数
    private Float min; // 最小值
    private Float max; // 最大值
    private Float mode; // 众数
    private LinkedHashMap<String,Integer> binData;
}
