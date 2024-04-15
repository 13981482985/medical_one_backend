package com.cqupt.software_1.controller;

import com.alibaba.fastjson.JSON;
import com.cqupt.software_1.common.R;
import com.cqupt.software_1.entity.Category2Entity;
import com.cqupt.software_1.entity.CategoryEntity;
import com.cqupt.software_1.service.Category2Service;
import com.cqupt.software_1.service.CategoryService;
import org.hibernate.event.spi.PreCollectionUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO 公共模块新增类
@RestController
@RequestMapping("/api")
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    Category2Service category2Service;
    // 获取目录
    @GetMapping("/category")
    public R<List<CategoryEntity>> getCatgory(){
        List<CategoryEntity> list = categoryService.getCategory();
        System.out.println(JSON.toJSONString(list));
        return R.success("200",list);
    }
    // 字段管理获取字段
    @GetMapping("/category2")
    public R <List<Category2Entity>> getCatgory2(){
        List<Category2Entity> list = category2Service.getCategory2();
        return R.success("200",list);
    }
    // 创建一种新的疾病
    @PostMapping("/addDisease")
    public R addDisease(@RequestBody CategoryEntity categoryNode){
        // 首先获取所有的已有疾病列表 判断是否重复
        List<CategoryEntity> list = categoryService.list(null);
        List<CategoryEntity> isRepeat = list.stream().filter(categoryEntity -> {
            return categoryEntity.getLabel().equals(categoryNode.getLabel()) && categoryEntity.getIsDelete()==0;
        }).collect(Collectors.toList());
        if(isRepeat!=null && isRepeat.size()>0){
            System.out.println("重复节点："+JSON.toJSONString(isRepeat));
            return R.fail(300,"该疾病已存在！");
        }
        categoryService.save(categoryNode);
        return R.success(200,"新增目录成功");
    }

    // 删除一个目录
    @GetMapping("/category/remove")
    public R removeCate(CategoryEntity categoryEntity){
        categoryService.removeNode(categoryEntity.getId());
        return R.success(200,"删除成功");
    }

    @GetMapping("/addParentDisease")
    public R addParentDisease(@RequestParam("diseaseName") String diseaseName){
        // 首先获取所有的已有疾病列表 判断是否重复
        List<CategoryEntity> list = categoryService.list(null);
        List<CategoryEntity> isRepeat = list.stream().filter(categoryEntity -> {
            return categoryEntity.getLabel().equals(diseaseName) && categoryEntity.getIsDelete()==0;
        }).collect(Collectors.toList());
        if(isRepeat!=null && isRepeat.size()>0){
            return R.fail(300,"该疾病已存在！");
        }
        categoryService.addParentDisease(diseaseName);
        return R.success(200,"新增疾病成功");
    }

    @GetMapping("/disease/all")
    public R getAllDisease(){
        List<CategoryEntity> category = categoryService.getCategory();
        List<CategoryEntity> notLeafCat = getNotLeafCat(category);
        // 同时去掉公共数据集节点
        Stream<CategoryEntity> allDiseaseCat = notLeafCat.stream().filter(categoryEntity -> {
            return !categoryEntity.getLabel().equals("公共数据集");
        });
        return R.success("200",allDiseaseCat);

    }
    private List<CategoryEntity> getNotLeafCat(List<CategoryEntity> category){
        // 删除每个叶子节点
        List<CategoryEntity> level1 = category.stream().filter(categoryEntity -> {
            return categoryEntity.getIsLeafs()!=1; //  返回一级目录下的所有非叶子节点
        }).collect(Collectors.toList());

        for (CategoryEntity categoryEntity : level1) {
            List<CategoryEntity> children = categoryEntity.getChildren();
            if(children!=null && children.size()>0){
                categoryEntity.setChildren(getNotLeafCat(children));
            }
        }
        return level1;
    }

    @GetMapping("/getTableNumber")
    public R getTableNumber(){
        List<CategoryEntity> list = categoryService.list();
        List<CategoryEntity> collect = list.stream().filter(categoryEntity -> {
            return categoryEntity.getIsLeafs()==1;
        }).collect(Collectors.toList());
        System.out.println("表数量为："+collect.size());
        return R.success("200",collect.size());
    }



}
