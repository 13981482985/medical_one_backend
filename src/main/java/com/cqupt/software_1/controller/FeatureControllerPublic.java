package com.cqupt.software_1.controller;


import com.alibaba.fastjson.JSON;

import com.cqupt.software_1.common.FeatureType;
import com.cqupt.software_1.common.R;
import com.cqupt.software_1.entity.FeatureEntity;
import com.cqupt.software_1.service.FeatureManageService;
import com.cqupt.software_1.vo.FeatureListVo;
import com.cqupt.software_1.vo.FeatureVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO 公共模块新增类
@RestController
@RequestMapping("/api/feature")
public class FeatureControllerPublic {
    @Autowired
    FeatureManageService featureManageService;
    @GetMapping("/getFeatures")
    public R<FeatureEntity> getFeture(@RequestParam("index") Integer belongType){ // belongType说明是属于诊断类型、检查类型、病理类型、生命特征类型
        String type = null;
        for (FeatureType value : FeatureType.values()) {
            if(value.getCode() == belongType){
                type = value.getName();
            }
        }
        List<FeatureVo> list = featureManageService.getFeatureList(type);
        return R.success("200",list);
    }


    // TODO 废弃方法
    @PostMapping("/insertFeature") // 上传特征分类结果
    public R fieldInsert(@RequestBody FeatureListVo featureListVo){
        System.out.println("tableHeaders:"+ JSON.toJSONString(featureListVo));

        featureManageService.insertFeatures(featureListVo);
        return null;
    }
}
