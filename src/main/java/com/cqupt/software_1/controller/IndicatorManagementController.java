package com.cqupt.software_1.controller;


import com.alibaba.fastjson.JSON;
import com.cqupt.software_1.common.R;
import com.cqupt.software_1.entity.IndicatorManageEntity;
import com.cqupt.software_1.service.IndicatorManagementService;
import com.cqupt.software_1.vo.DataFillMethodVo;
import com.cqupt.software_1.vo.IndicatorsMissDataVo;
import com.cqupt.software_1.vo.IndicatorsMissDataVos;
import com.cqupt.software_1.vo.IsFillVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class IndicatorManagementController {

    @Autowired
    IndicatorManagementService indicatorManagementService;

    @GetMapping("/getIndicators")
    private R<List<IndicatorManageEntity>> getIndicators(@RequestParam("types") List<String> types, @RequestParam("tableName") String tableName){
        System.out.println("types:"+types);
        List<IndicatorManageEntity> list = indicatorManagementService.getIndicators(types,tableName);
        System.out.println("feature:"+list);
        return R.success("200",list);
    }
    @PostMapping("/getIndicatorsInfo")
    public R<List<IndicatorsMissDataVo>> getIndicatorsInfo(@RequestBody IndicatorsMissDataVos indicatorsMissDataVos){
        System.out.println("參數："+JSON.toJSONString(indicatorsMissDataVos));
        List<IndicatorsMissDataVo> list = indicatorManagementService.getIndicatorsInfo(indicatorsMissDataVos.getCheckedFeats(),indicatorsMissDataVos.getTableName());
        return R.success("200",list);
    }

    // 跟据不同的算法进行数据补齐
    @PostMapping("/fillData")
    public R fillData(@RequestBody DataFillMethodVo dataFillMethodVo) {
        System.out.println("参数为："+dataFillMethodVo);
        List<Map<String, IsFillVo>> list = indicatorManagementService.fillData(dataFillMethodVo);
        return R.success("200", list);
    }
}
