package com.cqupt.software_1.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IsFillVo {
    private String colName;
    private String value;
    private Boolean flag;
}
