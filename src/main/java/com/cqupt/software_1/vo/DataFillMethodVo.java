package com.cqupt.software_1.vo;

import com.cqupt.software_1.entity.CreateTaskEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataFillMethodVo {
    private String tableName;
    private List<IndicatorsMissDataVo> missCompleteMethod;
    private CreateTaskEntity newTaskInfo;
}
