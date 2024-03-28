package com.cqupt.software_1.vo;


import lombok.Data;

import java.util.List;

@Data
public class UpdateFiledVO {

    // 疾病名称
    private String diseaseName;

    // 字段名字列表
    private List<String> fileds;
}
