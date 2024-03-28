package com.cqupt.software_1.mapper;

import com.cqupt.software_1.entity.IndicatorManageEntity;
import com.cqupt.software_1.vo.IndicatorsMissDataVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IndicatorManagementMapper {
    List<IndicatorManageEntity> getIndicators(@Param("types") List<String> types);

    float getMissRate(String featureName, String tableName);

    IndicatorsMissDataVo getMissDataInfo(@Param("checkedFeat") IndicatorManageEntity checkedFeat, @Param("tableName") String tableName);
}
