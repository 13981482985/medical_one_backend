package com.cqupt.software_1.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleAnalyzeVo {

    private List<DiscreteVo> discreteVos;

    private NotDiscrete notDiscrete;
    private NotDiscrete notDiscrete2;

    private Double wilcoxonW;
    private Double wilcoxonP;

    private Double tT;   // t检验
    private Double tP;

    private Double correctTT;  // 矫正t检验
    private Double correctTP;
}
