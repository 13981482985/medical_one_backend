package com.cqupt.software_1.controller;

import com.cqupt.software_1.common.R;
import com.cqupt.software_1.entity.FieldManagementEntity;
import com.cqupt.software_1.service.FieldManagementService;
import com.cqupt.software_1.vo.QueryFiledVO;
import com.cqupt.software_1.vo.UpdateFiledVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


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

}
