package com.cqupt.software_1.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.software_1.common.R;
import com.cqupt.software_1.entity.CategoryEntity;
import com.cqupt.software_1.entity.CreateTaskEntity;
import com.cqupt.software_1.service.CategoryService;
import com.cqupt.software_1.service.TableDataService;
import com.cqupt.software_1.service.TableDescribeService;
import com.cqupt.software_1.service.UserService;
import com.cqupt.software_1.vo.*;
import com.google.gson.Gson;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public R getDesTableData(@RequestParam("featureName") String featureName, @RequestParam("tableName") String tableName, @RequestParam(value = "taskInfo",required = false) String taskInfo) throws IOException, URISyntaxException {
        // 将JSON转成对象
        Gson gson = new Gson();
        CreateTaskEntity createTaskEntity = gson.fromJson(taskInfo, CreateTaskEntity.class);
        System.err.println("描述性分析任务参数："+createTaskEntity);
        FeatureDescAnaVo featureDescAnaVo =  tableDataService.featureDescAnalyze(featureName,tableName,createTaskEntity);
        return R.success("200",featureDescAnaVo);
    }

    // TODO 单因素分析
    @GetMapping("/singleFactorAnalyze")
    public R getSingleFactorAnalyze(@RequestParam("tableName") String tableName, @RequestParam("colNames") List<String> colNames,@RequestParam(value = "taskInfo",required = false)String taskInfo) throws IOException, URISyntaxException {
        System.out.println("表名："+tableName+" 列表："+colNames);
        Gson gson = new Gson();
        CreateTaskEntity createTaskEntity = gson.fromJson(taskInfo, CreateTaskEntity.class);
        System.err.println("createTaskEntity"+createTaskEntity);
        SingleAnalyzeVo singleAnalyzeVo = tableDataService.singleFactorAnalyze(tableName,colNames,createTaskEntity);
        return R.success("200",singleAnalyzeVo);
    }

    // TODO 一致性验证
    @GetMapping("/consistencyAnalyze")
    public R getConsistencyAnalyze(@RequestParam("tableName") String tableName, @RequestParam("featureName") String featureName,@RequestParam(value = "taskInfo",required = false)String taskInfo) throws IOException, URISyntaxException {
        CreateTaskEntity createTaskEntity = new Gson().fromJson(taskInfo, CreateTaskEntity.class);
        System.err.println("任务信息:"+createTaskEntity);
        ConsistencyAnalyzeVo consistencyAnalyze = tableDataService.consistencyAnalyze(tableName,featureName,createTaskEntity);
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

    // 获取首页疾病数据统计信息
    @GetMapping("/getStaticDisease")
    public R getStaticDisease(){
        //
        List<CategoryEntity> list = categoryService.list(null);
        // 查询这些疾病下的所有表信息
        list = list.stream().filter(categoryEntity -> {
            return categoryEntity.getIsCommon() == 0;
        }).collect(Collectors.toList()); // 找到专病数据库

        Map<String, List<CategoryEntity>> resultMap = new HashMap<>();

        for (CategoryEntity category : list) { // 找到病库中的所有顶级目录
            if (category.getCatLevel()==2 && category.getIsDelete()==0) { // 找到一级目录
                List<CategoryEntity> leafNodes = new ArrayList<>();
                findLeafNodes(category, list, leafNodes); // 找到一级目录下的叶子节点
                resultMap.put(category.getLabel(), leafNodes);
            }
        }
        Set<String> keySet = resultMap.keySet();
        ArrayList<DiseaseDataVo> diseaseDataVos = new ArrayList<>();
        for (String key : keySet) {
            DiseaseDataVo diseaseDataVo = new DiseaseDataVo(key,tableDataService.getDataCount(resultMap.get(key)));
            diseaseDataVos.add(diseaseDataVo);
        }
        System.err.println("统计结果返回值："+diseaseDataVos);
        return R.success("200",diseaseDataVos);
    }

    private void findLeafNodes(CategoryEntity parent, List<CategoryEntity> categoryList, List<CategoryEntity> leafNodes) {
        for (CategoryEntity category : categoryList) {
            if (category.getParentId() != null && category.getParentId().equals(parent.getId()) && category.getIsDelete()==0) {
                if (category.getIsLeafs()==1) {
                    leafNodes.add(category);
                } else {
                    findLeafNodes(category, categoryList, leafNodes);
                }
            }
        }
    }

}
