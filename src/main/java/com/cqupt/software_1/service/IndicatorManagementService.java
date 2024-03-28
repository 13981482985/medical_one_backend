package com.cqupt.software_1.service;

import com.cqupt.software_1.entity.IndicatorManageEntity;
import com.cqupt.software_1.vo.DataFillMethodVo;
import com.cqupt.software_1.vo.IndicatorsMissDataVo;
import com.cqupt.software_1.vo.IsFillVo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IndicatorManagementService {
    List<IndicatorManageEntity> getIndicators(List<String> types,String tableName);

    List<IndicatorsMissDataVo> getIndicatorsInfo(List<IndicatorManageEntity> checkedFeats, String tableName);

    List<Map<String,IsFillVo>> fillData(DataFillMethodVo dataFillMethodVo);
}
