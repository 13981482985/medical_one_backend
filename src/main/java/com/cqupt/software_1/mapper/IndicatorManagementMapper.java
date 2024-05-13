package com.cqupt.software_1.mapper;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cqupt.software_1.entity.IndicatorManageEntity;
import com.cqupt.software_1.vo.IndicatorsMissDataVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface IndicatorManagementMapper {
    List<IndicatorManageEntity> getIndicators(@Param("types") List<String> types);

    float getMissRate(@Param("featureName") String featureName, @Param("tableName") String tableName);

    IndicatorsMissDataVo getMissDataInfo(@Param("checkedFeat") IndicatorManageEntity checkedFeat, @Param("tableName") String tableName);

    List<Map<String, String>> getTableFeilds(String tableName);

    Map<String, Long> getFiledCount(@Param("tableField") String tableField, @Param("tableName") String tableName);

    String getColType(@Param("tableName")String tableName, @Param("featureName") String featureName);
}
