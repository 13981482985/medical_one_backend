package com.cqupt.software_1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software_1.entity.CategoryEntity;


import java.util.List;

// TODO 公共模块新增类
public interface CategoryService extends IService<CategoryEntity> {
    List<CategoryEntity> getCategory();

    void removeNode(String id);

    void addParentDisease(String diseaseName);
}
