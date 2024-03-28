package com.cqupt.software_1.controller;

import com.alibaba.fastjson.JSON;
import com.cqupt.software_1.common.R;
import com.cqupt.software_1.entity.IndicatorCategory;
import com.cqupt.software_1.service.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IndicatorController {
    @Autowired
    private IndicatorService indicatorService;
    @GetMapping("/indicatorCategory")
    public R<List<IndicatorCategory>> getIndicatorCategory(){
        List<IndicatorCategory> list = indicatorService.getIndicatorCattegory();
        return R.success("200",list);
    }
}
