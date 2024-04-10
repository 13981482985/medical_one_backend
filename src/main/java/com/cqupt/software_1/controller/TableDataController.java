package com.cqupt.software_1.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.software_1.common.R;
import com.cqupt.software_1.entity.CategoryEntity;
import com.cqupt.software_1.service.CategoryService;
import com.cqupt.software_1.service.TableDataService;
import com.cqupt.software_1.service.TableDescribeService;
import com.cqupt.software_1.service.UserService;
import com.cqupt.software_1.vo.*;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO 公共模块新增类

@RestController()
@RequestMapping("/api")
public class TableDataController {

    @Autowired
    TableDataService tableDataService;
    @Autowired
    UserService userService;

    @Autowired
    CategoryService categoryService;
    @Autowired
    TableDescribeService tableDescribeService;
    @GetMapping("/getTableData")
    public R getTableData(@RequestParam("tableId") String tableId, @RequestParam("tableName") String tableName){
        List<LinkedHashMap<String, Object>> tableData = tableDataService.getTableData(tableId, tableName);
        return R.success("200",tableData);
    }

    // TODO 文件上传
    @PostMapping("/dataTable/upload")
    public R uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("newName") String tableName,
                             @RequestParam("disease") String type,
                             @RequestParam("user") String user,
                             @RequestParam("uid") int userId,
                             @RequestParam("parentId") String parentId,
                             @RequestParam("parentType") String parentType){
        // 保存表数据信息
        try {
            List<String> featureList = tableDataService.uploadFile(file, tableName, type, user, userId, parentId, parentType);
            return R.success("200",featureList); // 返回表头信息
        }catch (Exception e){
            e.printStackTrace();
            return R.success(500,"文件上传异常");
        }
    }

    // TODO 检查上传文件是数据文件的表名在数据库中是否重复
    @GetMapping("/DataTable/inspection")
    public R tableInspection(@RequestParam("newname") String name){
        List<CategoryEntity> list = categoryService.list(new QueryWrapper<CategoryEntity>(null));
        boolean flag = true;
        for (CategoryEntity categoryEntity : list) {
            if(categoryEntity.getLabel().equals(name)) {
                flag = false;
                break;
            }
        }
        return R.success("200",flag); // 判断文件名是否重复
    }


    // 筛选数据后，创建表保存筛选后的数据
    @PostMapping("/createTable")
    public R createTable(@RequestBody FilterTableDataVo filterTableDataVo){
         tableDataService.createTable(filterTableDataVo.getAddDataForm().getDataName(),filterTableDataVo.getAddDataForm().getCharacterList(),
                filterTableDataVo.getAddDataForm().getCreateUser(),filterTableDataVo.getNodeData());
        return R.success(200,"SUCCESS");
    }

    // 根据条件筛选数据
    @PostMapping("/filterTableData")
    public R <List<Map<String,Object>>> getFilterTableData(@RequestBody FilterTableDataVo filterTableDataVo){
        List<LinkedHashMap<String, Object>> filterDataByConditions = tableDataService.getFilterDataByConditions(filterTableDataVo.getAddDataForm().getCharacterList(), filterTableDataVo.getNodeData());
        return R.success("200",filterDataByConditions);
    }

    @PostMapping("/exportFile")
    public R exportFile(@RequestBody ExportFilledDataTableVo dataFillMethodVo) throws IOException {
        List<String> fileData = tableDataService.exportFile(dataFillMethodVo);
        String fileStr = fileData.stream().collect(Collectors.joining("\n"));
        return R.success("200",fileStr);
    }

    // TODO 列描述性分析
    @GetMapping("/tableDesAnalyze")
    public R getDesTableData(@RequestParam("featureName") String featureName, @RequestParam("tableName") String tableName) throws IOException, URISyntaxException {
        FeatureDescAnaVo featureDescAnaVo =  tableDataService.featureDescAnalyze(featureName,tableName);
        return R.success("200",featureDescAnaVo);
    }

    // TODO 单因素分析
    @GetMapping("/singleFactorAnalyze")
    public R getSingleFactorAnalyze(@RequestParam("tableName") String tableName, @RequestParam("colNames") List<String> colNames) throws IOException, URISyntaxException {
        System.out.println("表名："+tableName+" 列表："+colNames);
        SingleAnalyzeVo singleAnalyzeVo = tableDataService.singleFactorAnalyze(tableName,colNames);
        return R.success("200",singleAnalyzeVo);
    }

    // TODO 一致性验证
    @GetMapping("/consistencyAnalyze")
    public R getConsistencyAnalyze(@RequestParam("tableName") String tableName, @RequestParam("featureName") String featureName) throws IOException, URISyntaxException {
        ConsistencyAnalyzeVo consistencyAnalyze = tableDataService.consistencyAnalyze(tableName,featureName);
        System.out.println(JSON.toJSONString(consistencyAnalyze));
        if(consistencyAnalyze!=null){
            return R.success("200",consistencyAnalyze);
        }else{
            return R.fail(500,"数据异常，无法分析");
        }
    }

    // TODO 根据字段列表和表名导出数据
    @GetMapping("/getTableDataByFields")
    public R getTableDataByFields(@RequestParam("tableName") String tableName, @RequestParam("featureList") List<String> featureList){
        List<Map<String, Object>> tableDataByFields = tableDataService.getTableDataByFields(tableName,featureList);
        // 将数据变成list<String>类型 每个list代表一行数据，中间用逗号隔开
        List<String> fileData = new ArrayList<>();
        StringBuffer tableHead = new StringBuffer();
        for(int i=0; i<featureList.size(); i++){
            if(i!=featureList.size()-1){
                tableHead.append(featureList.get(i)+",");
            }else{
                tableHead.append(featureList.get(i));
            }
        }
        fileData.add(tableHead.toString());
        for (Map<String, Object> rowData: tableDataByFields) {
            StringBuffer temp = new StringBuffer();
            for(int i=0; i<featureList.size(); i++){
                if(i!=featureList.size()-1) {
                    temp.append(rowData.get(featureList.get(i))+",");
                }else{
                    temp.append(rowData.get(featureList.get(i)));
                }
            }
            fileData.add(temp.toString());
        }
        String fileStr = fileData.stream().collect(Collectors.joining("\n"));
        return R.success("200",fileStr);
    }

}
