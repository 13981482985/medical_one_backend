package com.cqupt.software_1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software_1.entity.Category2Entity;
import com.cqupt.software_1.mapper.Category2Mapper;
import com.cqupt.software_1.service.Category2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO 公共模块新增类

@Service
public class Category2ServiceImpl extends ServiceImpl<Category2Mapper, Category2Entity>
        implements Category2Service {

    @Autowired
    Category2Mapper dataManagerMapper;


    @Autowired
    Category2Mapper category2Mapper;

    @Override
    public List<Category2Entity> getCategory() {

        // 获取所有目录行程树形结构
        List<Category2Entity> categoryEntities = dataManagerMapper.selectList(null);
        // 获取所有级结构
        List<Category2Entity> treeData = categoryEntities.stream().filter((Category2Entity) -> {
            return Category2Entity.getParentId().equals("0") && Category2Entity.getIsDelete()==0;
        }).map((level1Cat) -> {
            level1Cat.setChildren(getCatChildren(level1Cat, categoryEntities));;
            return level1Cat;
        }).collect(Collectors.toList());

        return treeData;
    }





    @Override
    public List<Category2Entity> getCategory2() {

        // 获取所有目录行程树形结构
        List<Category2Entity> categoryEntities = dataManagerMapper.selectList(null);
        // 获取所有级结构
        List<Category2Entity> treeData = categoryEntities.stream().filter((Category2Entity) -> {
            return Category2Entity.getParentId().equals("0") && Category2Entity.getIsDelete()==0;
        }).map((level1Cat) -> {
            level1Cat.setChildren(getSecondLevelChildren(level1Cat.getId()));;
            return level1Cat;
        }).collect(Collectors.toList());

        // 顶层以下都变为叶子结点
        for (Category2Entity treeDatum : treeData) {
            for (Category2Entity child : treeDatum.getChildren()) {
                child.setIsLeafs(1);
            }

        }
        return treeData;
    }


    // 获取第二层目录
    private List<Category2Entity> getSecondLevelChildren(String parentId) {
        // 获取所有第二层目录
        List<Category2Entity> secondLevelCategories = dataManagerMapper.selectList(null).stream()
                .filter(Category2Entity -> Category2Entity.getParentId().equals(parentId) && Category2Entity.getIsDelete() == 0)
                .map(level2Cat -> {
                    level2Cat.setChildren(new ArrayList<>()); // 第二层目录没有子目录
                    return level2Cat;
                })
                .collect(Collectors.toList());

        return secondLevelCategories;
    }


    @Override
    public void removeNode(String id) {
        category2Mapper.removeNode(id);
    }

    // 获取1级目录下的所有子结构
    private List<Category2Entity> getCatChildren(Category2Entity level1Cat, List<Category2Entity> categoryEntities) {
        List<Category2Entity> children = categoryEntities.stream().filter((CategoryEntity) -> {
            return CategoryEntity.getParentId().equals(level1Cat.getId()) && CategoryEntity.getIsDelete()==0; // 获取当前分类的所有子分类
        }).map((child) -> {
            // 递归设置子分类的所有子分类
            child.setChildren(getCatChildren(child, categoryEntities));
            return child;
        }).collect(Collectors.toList());
        return children;
    }
}
