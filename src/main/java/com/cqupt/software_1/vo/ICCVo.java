package com.cqupt.software_1.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ICCVo {
    private String method = "ICC1:one-way random-effects model";
    private String type = "ICC1";
    private Double ICC;
    private Double F;
    private Integer df1;
    private Integer df2;
    private Double p;
}
