package com.cqupt.software_1.vo;

import com.cqupt.software_1.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO 公共模块新增类

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterTableDataVo {
    private AddDataFormVo addDataForm;
    private CategoryEntity nodeData;

    // TODO 新增
    private String nodeid;
    private String status;
}
