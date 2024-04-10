package com.cqupt.software_1.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.software_1.common.R;
import com.cqupt.software_1.entity.FieldManagementEntity;
import com.cqupt.software_1.service.FieldManagementService;
import com.cqupt.software_1.vo.QueryFiledVO;
import com.cqupt.software_1.vo.UpdateFiledVO;
import com.sun.org.apache.bcel.internal.generic.ARETURN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/filed")
public class FieldManagementController {

    @Autowired
    FieldManagementService fieldManagementService;
    /**
     *
     * 通过关联疾病名称展示字段信息
     * @param
     * @return 字段信息表
     */
    @PostMapping("/getAllFiled")
    public R getAllFiled(@RequestBody QueryFiledVO queryFiledVO){
        System.out.println(queryFiledVO.getDiseaseName());
        List<FieldManagementEntity> res = fieldManagementService.getFiledByDiseaseName(queryFiledVO.getDiseaseName());
        return R.success(res);
    }

    /**
     *
     * 新建特征表 根据动态选择来更新字段表
     *
     * 接收病种名字 和 字段列表
     */
    @PostMapping("/updateFiled")
    public R updateFiled(@RequestBody UpdateFiledVO updateFiledVO){
        String diseaseName = updateFiledVO.getDiseaseName();
        List<String> fields = updateFiledVO.getFileds();
        // 更新字段表信息
        fieldManagementService.updateFieldsByDiseaseName(diseaseName, fields);
        return R.success(null);
    }

    // 根据表名获取所有字段
    @GetMapping("/getAllFieldsByTableName")
    public R getAllFieldsByTableName(@RequestParam("tableName") String tableName){
        System.out.println("tableName: "+tableName);
        List<FieldManagementEntity> list = fieldManagementService.list(new QueryWrapper<FieldManagementEntity>().eq("table_name", "copd"));// TODO 字段管理表没有完善 先写死的
        List<String> featureList = list.stream().map(fieldManagementEntity -> {
            return fieldManagementEntity.getFeatureName();
        }).collect(Collectors.toList());
        System.out.println("featureList:"+featureList);
        return R.success("200",featureList);
    }

}
