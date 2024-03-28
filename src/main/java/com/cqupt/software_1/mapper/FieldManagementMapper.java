package com.cqupt.software_1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.cqupt.software_1.entity.FieldManagementEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// TODO 公共模块新增类

@Mapper
public interface FieldManagementMapper extends BaseMapper<FieldManagementEntity> {
    List<FieldManagementEntity> getFieldsByType(List<String> indexEnNames);
    List<FieldManagementEntity> getFiledByDiseaseName(@Param("diseaseName") String diseaseName);
    void updateFieldsByDiseaseName(@Param("diseaseName") String diseaseName, @Param("fields") List<String> fields);
}
