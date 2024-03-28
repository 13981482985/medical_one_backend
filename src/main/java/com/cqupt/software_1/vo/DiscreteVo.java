package com.cqupt.software_1.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



// 如果某一个列是离散的 就将每一个离散取值值信息保存到这个实体 比如性别列 有男女两种取值
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscreteVo {

    private String variable; // 离散取值 比如男，女
    private Integer frequent; // 女生取值数量


    /**描述性分析需要的**/
    private Float amount; // 女生占比
    private Float totalAmount; // 累计占比


    /**  单因素分析需要的 **/
    private Integer validFrequent; // 有效值
    private Integer missFrequent; // 缺失值

}
