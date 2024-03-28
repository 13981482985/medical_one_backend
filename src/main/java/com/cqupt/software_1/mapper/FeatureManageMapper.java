package com.cqupt.software_1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.software_1.entity.FeatureEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// TODO 公共模块新增类

@Mapper
public interface FeatureManageMapper extends BaseMapper<FeatureEntity> {
    List<FeatureEntity> selectFeatures(@Param("belongType") String belongType);
}
