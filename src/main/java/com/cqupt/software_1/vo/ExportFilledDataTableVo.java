package com.cqupt.software_1.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportFilledDataTableVo {
    private DataFillMethodVo dataFillMethodVo;
    private String path;
    private String fileName;
}
