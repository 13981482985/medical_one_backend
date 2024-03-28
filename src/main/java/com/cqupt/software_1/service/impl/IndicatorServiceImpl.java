package com.cqupt.software_1.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software_1.entity.CategoryEntity;
import com.cqupt.software_1.entity.IndicatorCategory;
import com.cqupt.software_1.mapper.IndicatorMapper;
import com.cqupt.software_1.service.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndicatorServiceImpl extends ServiceImpl<IndicatorMapper,IndicatorCategory> implements IndicatorService {
    @Autowired
    IndicatorMapper indicatorMapper;
    @Override
    public List<IndicatorCategory> getIndicatorCattegory() {
        List<IndicatorCategory> indicatorCategories = indicatorMapper.selectList(null);
        System.out.println("service========================");
        System.out.println(JSON.toJSONString(indicatorCategories));
        List<IndicatorCategory> treeData = indicatorCategories.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentId()==0;
        }).map((level1Cat) -> {
            level1Cat.setChildren(getCatChildren(level1Cat, indicatorCategories));;
            return level1Cat;
        }).collect(Collectors.toList());
        return treeData;
    }

    @Override
    public List<IndicatorCategory> getEnName(List<String> types) {
        List<IndicatorCategory> enNames = indicatorMapper.getEnNames(types);
        return enNames;
    }

    // 获取1级目录下的所有子结构
    private List<IndicatorCategory> getCatChildren(IndicatorCategory level1Cat, List<IndicatorCategory> categoryEntities) {
        List<IndicatorCategory> children = categoryEntities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentId()==level1Cat.getId(); // 获取当前分类的所有子分类
        }).map((child) -> {
            // 递归设置子分类的所有子分类
            child.setChildren(getCatChildren(child, categoryEntities));
            return child;
        }).collect(Collectors.toList());
        return children;
    }
}
