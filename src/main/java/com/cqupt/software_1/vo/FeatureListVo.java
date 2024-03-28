package com.cqupt.software_1.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// TODO 公共模块新增类

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureListVo {
    private String tableName;
    private List<FeatureClassVo> tableHeaders;
}
