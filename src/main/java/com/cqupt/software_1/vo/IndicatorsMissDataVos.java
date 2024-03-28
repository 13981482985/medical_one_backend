package com.cqupt.software_1.vo;

import com.cqupt.software_1.entity.IndicatorManageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndicatorsMissDataVos {
    private List<IndicatorManageEntity> checkedFeats;
    private String tableName;
}
