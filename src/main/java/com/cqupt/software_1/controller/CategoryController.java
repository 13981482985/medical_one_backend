package com.cqupt.software_1.controller;

import com.alibaba.fastjson.JSON;
import com.cqupt.software_1.common.R;
import com.cqupt.software_1.entity.Category2Entity;
import com.cqupt.software_1.entity.CategoryEntity;
import com.cqupt.software_1.service.Category2Service;
import com.cqupt.software_1.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        System.out.println("擦汗寻");
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
        System.out.println("参数为："+ JSON.toJSONString(categoryNode));
        categoryService.save(categoryNode);
        return R.success(200,"新增目录成功");
    }

    // 删除一个目录
    @GetMapping("/category/remove")
    public R removeCate(CategoryEntity categoryEntity){
        System.out.println("要删除的目录为："+JSON.toJSONString(categoryEntity));;
        categoryService.removeNode(categoryEntity.getId());
        return R.success(200,"删除成功");
    }

    @GetMapping("/addParentDisease")
    public R addParentDisease(@RequestParam("diseaseName") String diseaseName){
        categoryService.addParentDisease(diseaseName);
        return R.success("200",null);
    }



}
