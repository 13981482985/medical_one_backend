package com.cqupt.software_1.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteDiseaseVo {
    private List<String> deleteIds;
    private List<String> deleteNames;
    private String username;
    private String uid;
}
