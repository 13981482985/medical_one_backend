package com.cqupt.software_1.controller;



import com.alibaba.fastjson.JSON;
import com.cqupt.software_1.Util.FieldStats;

import com.cqupt.software_1.common.R;
import com.cqupt.software_1.common.RunPyEntity;
import com.cqupt.software_1.common.RunPyR;
import com.cqupt.software_1.common.UserThreadLocal;
import com.cqupt.software_1.entity.FieldManagementEntity;
import com.cqupt.software_1.entity.TableManager;
import com.cqupt.software_1.entity.Task;
import com.cqupt.software_1.mapper.CategoryMapper;
import com.cqupt.software_1.service.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/feature")
public class FeatureController {

    @Autowired
    TaskService taskService;

    @Autowired
    private TableManagerService tableManagerService;

    @Autowired
    private PageService pageService;


    @Autowired
    FieldManagementService fieldManagementService;


    @Value("${pyPath}")
    private String pyPath;

    @Autowired
    CategoryMapper categoryMapper;


    /**
     * 根据表名获取属性列
     *
     * @return
     */
    @GetMapping("/getAllFiled/{tableName}")
    public R<Map<String,FieldManagementEntity>> getFiledByTableName(@PathVariable("tableName")String tableName){
//        // 查找字段管理表跟据表名查找到表的所有属性
//        List<String> colNames = tableManagerService.getFiledByTableName(tableName);
//        // 根据属性名字查找 中文描述，是否是找到每个属性是否是人口学 生理学 行为学
//        List<TableManager> tableManagers  = tableManagerService.getAllTableManagersByFiledName(colNames);
//        Map<String, TableManager> tableMap = new HashMap<>();
//        for (int i = 0 ; i< colNames.size() ; i++){
//            String filedName = colNames.get(i);
//            TableManager tableManager = tableManagers.get(i);
//            tableMap.put(filedName, tableManager);
//
//        }
//        // 将属性名字作为key  注释作为val 存入 resMap
//        return new R<>(200,"成功",tableMap);
        HashMap<String, FieldManagementEntity> tableMap  = new HashMap<>();
        List<FieldManagementEntity> featureList = fieldManagementService.list(null);
        for (FieldManagementEntity feature : featureList) {
            if("integer".equals(feature.getType()) || "double precision".equals(feature.getType())) tableMap.put(feature.getFeatureName(),feature);
        }
        return new R<>(200,"成功",tableMap);

    }

    /**
     * 根据表名获取到前面1000行信息
     *
     * @param tableName
     * @return
     */
    @GetMapping("/getInfoByTableName")
    public R<List<Map<String,Object>>> getInfoByTableName( String tableName, int page ){
        PageHelper.startPage(page, 10);
        List<Map<String,Object>> res =  pageService.getInfoByTableName(tableName);
        PageInfo pageInfo = new PageInfo(res);
        return new R<>(200,"成功",res,pageInfo.getPages());
    }



    /**
     * 根据选择的字段获取到前面1000行信息
     *
     * @param params 不定参数
     * @return
     */
    @GetMapping("/getInfoBySelectedFiled")
    public R<List<Map<String,Object>>> getInfoBySelectedFiled(int page, String tableName,@RequestParam("params") String[] params){
        PageHelper.startPage(page, 10);
        List<Map<String,Object>> res =  pageService.getInfoBySelectedFiled(tableName,params);

        PageInfo pageInfo = new PageInfo(res);

        return new R<>(200,"成功",res,pageInfo.getPages());
    }


    /**
     *
     * 统计每一个字段的缺失率 均值 方差
     * @return
     */
    @GetMapping("/getStatisticaldData/{tableName}")
    public R<Map<String, Object>> getStatisticaldData(@PathVariable String tableName){

        // 查询出每一个字段
        List<Map<String, Object>> dataList = pageService.getInfoByTableName(tableName);

        Map<String, Integer> missingCountMap = new HashMap<>();
        Map<String, Double> sumMap = new HashMap<>();
        Map<String, Double> squaredSumMap = new HashMap<>();
        int totalRecords = dataList.size();
        // 计算缺失值数量、求和以及平方和

        for (Map<String, Object> data : dataList) {

            for (String fieldName : data.keySet()) {
                Object value = data.get(fieldName);
                if (value == null) {
                    missingCountMap.put(fieldName, missingCountMap.getOrDefault(fieldName, 0) + 1);
                } else if (value instanceof Number) {
                    double fieldValue = ((Number) value).doubleValue();
                    sumMap.put(fieldName, sumMap.getOrDefault(fieldName, 0.0) + fieldValue);
                    squaredSumMap.put(fieldName, squaredSumMap.getOrDefault(fieldName, 0.0) + fieldValue * fieldValue);
                }
            }
        }

        // 计算缺失率、均值和方差
        Map<String, Object> fieldStatsMap = new HashMap<>();
        for (String fieldName : dataList.get(0).keySet()) {
            int missingCount = missingCountMap.getOrDefault(fieldName, 0);
            double missingRate = (double) missingCount / totalRecords * 100;
            double mean = sumMap.getOrDefault(fieldName, 0.0) / (totalRecords - missingCount);
            double variance = squaredSumMap.getOrDefault(fieldName, 0.0) / (totalRecords - missingCount) - mean * mean;
            FieldStats fieldStats = new FieldStats(missingRate, mean, variance);
            fieldStatsMap.put(fieldName, fieldStats);
        }

        return new R<>(200,"成功",fieldStatsMap);

    }


//    @GetMapping("/runAi/{AiName}")
//    public RunPyR<Map> runAi(@PathVariable String AiName , @RequestParam String[] params){
//
//        int n = params.length - 1;
//
//        List<Map> res= new ArrayList<>();
//        Map json1 = null;
//        // 创建一个Map对象用于存储结果
//        Map<Integer, Double> map = new HashMap<>();
//        try {
//
//            String baseCommand = "python " + "F:\\mdical\\software_1\\src\\main\\resources\\py\\pca.py " ;
//
//            for (String param : params) {
//                baseCommand += param + " ";
//            }
//
//            baseCommand += n;
//
//            Process process = Runtime.getRuntime().exec(baseCommand);
//            // 获取 Python 执行的输出流
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            // 获取输出
//            String line1;
//            String line2;
//            // 读取输出流内容 第一行
//            line1 = reader.readLine();
//            System.out.println("line1 = " + line1);
//            Gson gson = new Gson();
//            // 转换为map格式
//            json1 = gson.fromJson(line1, Map.class);
//            res.add(json1);
//            int lineNum = 1;
//            while ((line2 = reader.readLine() )!= null){
//                if (lineNum >= 1){
//                    // 去除字符串两边的括号
//                    String content = line2.substring(1, line2.length() - 1);
//                    String[] split = content.split(",");
//                    map.put(Integer.parseInt(split[0]),Double.parseDouble(split[1]));
//                }
//                lineNum++;
//            }
//
//            System.out.println(map);
//            res.add(map);
//            // 等待 Python 执行完成
//            int exitCode = process.waitFor();
//            System.out.println("Python执行完成，退出码：" + exitCode);
//
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//
//        return new RunPyR<>(200,"成功",res);
//    }



    @PostMapping("/runAi")
    public RunPyR runAi(@RequestBody RunPyEntity runPyEntity) throws URISyntaxException, IOException {
        // 算法服务地址
        String baseUri = "http://localhost:5000/";
        // 运行哪个算法就是哪个路径
        URI uri = new URI(baseUri+runPyEntity.getAiName());
        // 创建Http 请求post
        HttpPost httpPost = new HttpPost(uri);
        HttpClient httpClient = HttpClients.createDefault();
        // 将参数列表转换为json
//        List<String> runParams = runPyEntity.getRunParams();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(runPyEntity);

        httpPost.setEntity(new StringEntity(json, StandardCharsets.UTF_8));
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

        // 执行post请求
        HttpResponse response = httpClient.execute(httpPost);
        // 获取返回结果
        String responseBody = EntityUtils.toString(response.getEntity());
        JsonNode jsonNode = objectMapper.readValue(responseBody,JsonNode.class);
//        // 创建任务
//        List<Task> list = taskService.list(null);
//        List<String> runParams = runPyEntity.getRunParams();
//        String features = runParams.stream().collect(Collectors.joining(","));
//        List<Task> isRepeat = list.stream().filter(task -> {
//            return runPyEntity.getAiName().equals(task.getModel()) && task.getFeature().equals(features) && task.getDataset().equals(runPyEntity.getTableName());
//        }).collect(Collectors.toList());
//        if(isRepeat == null || isRepeat.size()==0){
//            // 创建任务
//            Task task = new Task();
//            task.setModel(runPyEntity.getAiName());
//            task.setDataset(runPyEntity.getTableName());
//            task.setTaskname("疾病特征表征");
//            task.setLeader(UserThreadLocal.get().getUsername());
//            task.setCreatetime(new Timestamp(System.currentTimeMillis()));
//            String label = categoryMapper.getParentLabelByLabel(runPyEntity.getTableName());
//            task.setDisease(label);
//            task.setRemark("疾病特征表征");
//            task.setUserid(UserThreadLocal.get().getUid());
//            task.setFeature(features);
//            task.setTargetcolumn(features);
//            taskService.save(task);
//        }
        return new RunPyR<>(200,"成功",jsonNode);
    }



}
