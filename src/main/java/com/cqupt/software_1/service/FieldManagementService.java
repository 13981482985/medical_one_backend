package com.cqupt.software_1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software_1.entity.FieldManagementEntity;

import java.util.List;


// TODO 公共模块新增类
public interface FieldManagementService extends IService<FieldManagementEntity> {
    List<FieldManagementEntity> getFieldsByType(List<String> indexEnNames);
    List<FieldManagementEntity> getFiledByDiseaseName(String diseaseName);
    void updateFieldsByDiseaseName(String diseaseName, List<String> fields);
}
