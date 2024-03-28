package com.cqupt.software_1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.software_1.entity.IndicatorCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IndicatorMapper extends BaseMapper<IndicatorCategory> {
    List<IndicatorCategory> getEnNames(List<String> types);
}
