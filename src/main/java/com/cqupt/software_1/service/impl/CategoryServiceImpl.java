package com.cqupt.software_1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software_1.common.UserThreadLocal;
import com.cqupt.software_1.entity.CategoryEntity;
import com.cqupt.software_1.entity.UserLog;
import com.cqupt.software_1.mapper.CategoryMapper;
import com.cqupt.software_1.service.CategoryService;
import com.cqupt.software_1.service.UserLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

// TODO 公共模块新增类

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity>
        implements CategoryService {

    @Autowired
    CategoryMapper dataManagerMapper;
    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    UserLogService userLogService;
    @Override
    public List<CategoryEntity> getCategory() {

        // 获取所有目录行程树形结构
        List<CategoryEntity> categoryEntities = dataManagerMapper.selectList(null);
        // 获取所有级结构
        List<CategoryEntity> treeData = categoryEntities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentId().equals("0") && categoryEntity.getIsDelete()==0;
        }).map((level1Cat) -> {
            level1Cat.setChildren(getCatChildren(level1Cat, categoryEntities));;
            return level1Cat;
        }).collect(Collectors.toList());
        List<CategoryEntity> publicData = treeData.stream().filter(categoryEntity -> {
            return categoryEntity.getLabel().equals("公共数据集");
        }).collect(Collectors.toList());
        if(publicData!=null && publicData.size()>0){
            treeData.remove(publicData.get(0));
            treeData.add(publicData.get(0));
        }
        return treeData;
    }

    @Override
    public void removeNode(String id) {
        UserLog userLog = new UserLog();
        userLog.setOpTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        userLog.setUsername(UserThreadLocal.get().getUsername());
        userLog.setUid(UserThreadLocal.get().getUid());
        userLog.setOpType("删除病种数据信息");
        userLogService.save(userLog);
        categoryMapper.removeNode(id);
    }

    @Override
    public void addParentDisease(String diseaseName) {
        CategoryEntity categoryEntity = new CategoryEntity(null, 1, diseaseName, "0", 0, 0, "" + diseaseName, 0, 0, null);
        UserLog userLog = new UserLog();
        userLog.setOpTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        userLog.setUsername(UserThreadLocal.get().getUsername());
        userLog.setUid(UserThreadLocal.get().getUid());
        userLog.setOpType("添加新病种");
        userLogService.save(userLog);
        categoryMapper.insert(categoryEntity);
    }

    // 获取1级目录下的所有子结构
    private List<CategoryEntity> getCatChildren(CategoryEntity level1Cat, List<CategoryEntity> categoryEntities) {
        List<CategoryEntity> children = categoryEntities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentId().equals(level1Cat.getId()) && categoryEntity.getIsDelete()==0; // 获取当前分类的所有子分类
        }).map((child) -> {
            // 递归设置子分类的所有子分类
            child.setChildren(getCatChildren(child, categoryEntities));
            return child;
        }).collect(Collectors.toList());
        return children;
    }
}
