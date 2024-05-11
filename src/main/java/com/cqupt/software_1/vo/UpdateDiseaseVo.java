package com.cqupt.software_1.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDiseaseVo {
    private String categoryId;
    private String diseaseName;
    private String oldName;
    private String parentDisease;
    private String parentId;
    private String icdCode;
    private String username;
    private String uid;
}
