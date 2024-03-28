package com.cqupt.software_1.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class FeatureDescAnaVo {
    private Boolean discrete;
    private List<DiscreteVo> discreteVos;
    private NotDiscrete notDiscrete;
}
