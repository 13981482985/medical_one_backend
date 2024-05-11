package com.cqupt.software_1.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddDiseaseVo {
    private String firstDisease;
    //private String secondDisease;
    private String username;
    private String uid;

    private String parentId;
    private String icdCode;

}
