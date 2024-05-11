package com.cqupt.software_1.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.software_1.common.R;
import com.cqupt.software_1.common.Result;
import com.cqupt.software_1.entity.CategoryEntity;
import com.cqupt.software_1.entity.TableDescribeEntity;
import com.cqupt.software_1.service.CategoryService;
import com.cqupt.software_1.service.TableDescribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO 公共模块新增类

@RestController
@RequestMapping("/api")
public class TableDescribeController {
    @Autowired
    TableDescribeService tableDescribeService;

    @Autowired
    CategoryService categoryService;
    @GetMapping("/tableDescribe")
    public R<TableDescribeEntity> getTableDescribe(@RequestParam("id") String id){ // 参数表的Id
        TableDescribeEntity tableDescribeEntity = tableDescribeService.getOne(new QueryWrapper<TableDescribeEntity>().eq("table_id", id));
        // 根据表名获取字段数和行数
        Integer colCount = tableDescribeService.getColCount(tableDescribeEntity.getTableName());
        Integer rowCount = tableDescribeService.getRowCount(tableDescribeEntity.getTableName());
        tableDescribeEntity.setRowNumber(rowCount);
        tableDescribeEntity.setColNumber(colCount);
        return R.success("200",tableDescribeEntity);
    }

    @GetMapping("/tableDescribe/selectDataDiseases")
    public Result<TableDescribeEntity> selectDataDiseases(
//            @RequestParam("current_uid") String current_uid
    ){ // 参数表的Id
        List<CategoryEntity> res = categoryService.getLevel2Label();

        List<Object> retList = new ArrayList<>();
        for (CategoryEntity category : res) {
            Map<String, Object> ret =  new HashMap<>();
            ret.put("label", category.getLabel());
            ret.put("value", category.getId());
            if (selectCategoryDataDiseases(category.getId()).size() > 0) {
                ret.put("children", selectCategoryDataDiseases(category.getId()));
            }

            retList.add(ret);
        }


        return Result.success("200",retList);
//        return Result.success("200",adminDataManages);
    }

    public List<Map<String, Object>> selectCategoryDataDiseases(String pid){
        List<Map<String, Object>> retList = new ArrayList<>();
        List<CategoryEntity> res = categoryService.getLabelsByPid(pid);
        for (CategoryEntity category : res) {
            Map<String, Object> ret =  new HashMap<>();
            ret.put("label", category.getLabel());
            ret.put("value", category.getId());
            if (selectCategoryDataDiseases(category.getId()).size() > 0) {
                ret.put("children", selectCategoryDataDiseases(category.getId()));
            }
            retList.add(ret);
        }
        return retList;
    }

}
