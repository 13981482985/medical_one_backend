package com.cqupt.software_1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software_1.entity.CategoryEntity;
import com.cqupt.software_1.entity.IndicatorCategory;

import java.util.List;

public interface IndicatorService extends IService<IndicatorCategory> {
    List<IndicatorCategory> getIndicatorCattegory();

    List<IndicatorCategory> getEnName(List<String> types);
}
