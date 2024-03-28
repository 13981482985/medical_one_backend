package com.cqupt.software_1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.software_1.entity.CategoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

// TODO 公共模块新增类

@Mapper
public interface CategoryMapper extends BaseMapper<CategoryEntity> {
    void removeNode(@Param("id") String id);
}
