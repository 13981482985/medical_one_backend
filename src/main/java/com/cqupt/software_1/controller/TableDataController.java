package com.cqupt.software_1.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.software_1.common.R;
import com.cqupt.software_1.common.Result;
import com.cqupt.software_1.entity.*;
import com.cqupt.software_1.mapper.FilterDataColMapper;
import com.cqupt.software_1.mapper.FilterDataInfoMapper;
import com.cqupt.software_1.mapper.UserLogMapper;
import com.cqupt.software_1.service.CategoryService;
import com.cqupt.software_1.service.TableDataService;
import com.cqupt.software_1.service.TableDescribeService;
import com.cqupt.software_1.service.UserService;
import com.cqupt.software_1.vo.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
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
    @Autowired
    FilterDataInfoMapper filterDataInfoMapper;

    @Autowired
    FilterDataColMapper filterDataColMapper;

    @Autowired
    UserLogMapper userLogMapper;

    @GetMapping("/getFilterConditionInfos")
    public R getFilterInfo(){
        ArrayList<FilterConditionVo> vos = new ArrayList<>();
        List<FilterDataInfo> filterDataInfos = filterDataInfoMapper.selectList(null);
        for (FilterDataInfo filterDataInfo : filterDataInfos) {
            FilterConditionVo filterConditionVo = new FilterConditionVo();
            filterConditionVo.setFilterDataInfo(filterDataInfo);
            List<FilterDataCol> filterDataCols = filterDataColMapper.selectList(new QueryWrapper<FilterDataCol>().eq("filter_data_info_id", filterDataInfo.getId()));
            filterConditionVo.setFilterDataCols(filterDataCols);
            vos.add(filterConditionVo);
        }
        return R.success("200",vos);
    }
//    // 根据filterInfo的id的信息获取完整的筛选条件
//    @GetMapping("/getRecordFilterConditions")
//    public R getRecordFilterConditions(@RequestParam("filterInfoId") Integer filterInfoId){
//        List<FilterDataCol> filterDataCols = filterDataColMapper.selectList(new QueryWrapper<FilterDataCol>().eq("filter_data_info_id", filterInfoId));
//        return R.success("200",  filterDataCols);
//    }

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
                             @RequestParam("uid") Integer userId,
                             @RequestParam("parentId") String parentId,
                             @RequestParam("parentType") String parentType,
                             @RequestParam("status") String status) {
        // 保存表数据信息
        try {
            List<String> featureList = tableDataService.uploadFile(file, tableName, type, user, userId, parentId, parentType, status);
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
        FeatureDescAnaVo featureDescAnaVo =  tableDataService.featureDescAnalyze(featureName,tableName,createTaskEntity);
        return R.success("200",featureDescAnaVo);
    }

    // TODO 单因素分析
    @GetMapping("/singleFactorAnalyze")
    public R getSingleFactorAnalyze(@RequestParam("tableName") String tableName, @RequestParam("colNames") List<String> colNames,@RequestParam(value = "taskInfo",required = false)String taskInfo) throws IOException, URISyntaxException {
        Gson gson = new Gson();
        CreateTaskEntity createTaskEntity = gson.fromJson(taskInfo, CreateTaskEntity.class);
        SingleAnalyzeVo singleAnalyzeVo = tableDataService.singleFactorAnalyze(tableName,colNames,createTaskEntity);
        return R.success("200",singleAnalyzeVo);
    }

    // TODO 一致性验证
    @GetMapping("/consistencyAnalyze")
    public R getConsistencyAnalyze(@RequestParam("tableName") String tableName, @RequestParam("featureName") String featureName,@RequestParam(value = "taskInfo",required = false)String taskInfo) throws IOException, URISyntaxException {
        CreateTaskEntity createTaskEntity = new Gson().fromJson(taskInfo, CreateTaskEntity.class);
        ConsistencyAnalyzeVo consistencyAnalyze = tableDataService.consistencyAnalyze(tableName,featureName,createTaskEntity);
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
        // 每行map长度补齐
        for (Map<String, Object> row : tableDataByFields) {
            if(row==null) row = new HashMap<String, Object>();
            for (String field : featureList) {
                if(!row.containsKey(field)){
                    row.put(field,"");
                }
            }
        }
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
        for (Map<String, Object> rowData: tableDataByFields) { // 遍历每一行
            if(rowData==null) continue;
            StringBuffer temp = new StringBuffer();
            for(int i=0; i<featureList.size(); i++){
                if(i!=featureList.size()-1) {
                    temp.append(rowData.get(featureList.get(i))==null?"":rowData.get(featureList.get(i))+",");
                }else{
                    String data = rowData.get(featureList.get(i).toString()).toString();
                    if(data==null) data="";
                    temp.append(data);
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
            return categoryEntity.getIsCommon()==null || categoryEntity.getIsCommon() == 0;
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





    @GetMapping("/getInfoByTableName/{tableName}")
    public Result<List<Map<String,Object>>> getInfoByTableName(@PathVariable("tableName") String tableName){
        tableName = tableName.replace("\"", "");
        List<Map<String, Object>> res = tableDataService.getInfoByTableName(tableName);
        return Result.success(200, "成功", res);
    }

    @GetMapping("/getCountByTableName/{tableName}")
    public Result<List<Map<String,Object>>> getCountByTableName(@PathVariable("tableName") String tableName){
        tableName = tableName.replace("\"", "");
        Integer res = tableDataService.getCountByTableName(tableName);
        return Result.success(200, "成功", res);
    }


    // TODO 模糊匹配全字段数据信息
    @GetMapping("/getDiseasesInfo")
    public Result<List<Map<String, Object>>> getDiseasesInfo(@RequestParam("tableName") String tableName, @RequestParam("value") String value, @RequestParam("page") Integer page){
        // 手动搜索表的所有字段
        List<String> tableFields =  tableDataService.getTableFields(tableName);
        PageHelper.startPage(page, 10);
        System.out.println("所有字段长度："+tableFields.size());
        // 模糊匹配所有字段的数据
        List<LinkedHashMap<String, Object>> data = tableDataService.getDataLikeMatch(tableName,tableFields,value);
        PageInfo<Map> pageInfo = new PageInfo(data);
        int start = (page - 1) * 10;
        int end = Math.min(start + 10, pageInfo.getList().size());
        List<Map> currentPageData = pageInfo.getList().subList(start, end);

        // 构建返回结果，包含总数据条数和当前页数据
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageInfo.getTotal());
        result.put("data", currentPageData);

        return Result.success("200",result);
    }



    // TODO 新增
    @PostMapping("/createFilterBtnTable")
    public Result createFilterBtnTable(@RequestBody FilterTableDataVo filterTableDataVo,
                                       @RequestHeader("uid") String userId,
                                       @RequestHeader("username") String username,
                                       @RequestHeader("role") Integer role){
        tableDataService.createFilterBtnTable(filterTableDataVo.getAddDataForm().getDataName(),filterTableDataVo.getAddDataForm().getCharacterList(),
                filterTableDataVo.getAddDataForm().getCreateUser(),filterTableDataVo.getStatus(),filterTableDataVo.getAddDataForm().getUid(),filterTableDataVo.getAddDataForm().getUsername(),filterTableDataVo.getAddDataForm().getIsFilter(),filterTableDataVo.getAddDataForm().getIsUpload(),filterTableDataVo.getAddDataForm().getUid_list(),filterTableDataVo.getNodeid());
        UserLog userLog = new UserLog();
        // userLog.setId(1);
        userLog.setUsername(username);
        userLog.setUid(Integer.parseInt(userId));
//        userLog.setRole(role);
        userLog.setOpTime(new SimpleDateFormat("yyyy-MM-dd MM:HH:ss").format(new Date()));
        userLog.setOpType("用户建表"+filterTableDataVo.getAddDataForm().getDataName());
        userLogMapper.insert(userLog);
        return Result.success(200,"SUCCESS");
    }

    @PostMapping("/getFilterDataByConditionsByDieaseId")
    public Result<List<Map<String,Object>>> getFilterDataByConditionsByDieaseId(@RequestBody FilterTableDataVo filterTableDataVo){
        List<LinkedHashMap<String, Object>> filterDataByConditions = tableDataService.getFilterDataByConditionsByDieaseId(filterTableDataVo.getAddDataForm().getCharacterList(), filterTableDataVo.getAddDataForm().getUid(),filterTableDataVo.getAddDataForm().getUsername(),filterTableDataVo.getNodeid());
        return Result.success("200",filterDataByConditions);
    }

}
