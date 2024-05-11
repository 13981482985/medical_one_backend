package com.cqupt.software_1.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.software_1.common.RunPyEntity;
import com.cqupt.software_1.common.UserThreadLocal;
import com.cqupt.software_1.entity.*;
import com.cqupt.software_1.mapper.*;
import com.cqupt.software_1.service.*;
import com.cqupt.software_1.utils.HTTPUtils;
import com.cqupt.software_1.vo.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

// TODO 公共模块

@Service
public class TableDataServiceImpl implements TableDataService {
    private volatile boolean hasError = false;
    @Autowired
    IndicatorManagementMapper indicatorManagementMapper;

    @Autowired
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    DataSourceTransactionManager transactionManager;

    @Autowired
    TableDataMapper tableDataMapper;

    @Autowired
    TableDescribeMapper tableDescribeMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    UserLogService userLogService;

    @Autowired
    TaskService taskService;

    @Autowired
    FieldManagementService fieldManagementService;

    @Autowired
    StatisticalDataMapper statisticalDataMapper;

    @Autowired
    IndicatorManagementService indicatorManagementService;

    @Autowired
    FilterDataColMapper filterDataColMapper;
    @Autowired
    FilterDataInfoMapper filterDataInfoMapper;
    @Override
    public List<LinkedHashMap<String, Object>> getTableData(String TableId, String tableName) {
        List<LinkedHashMap<String, Object>> tableData = tableDataMapper.getTableData(tableName);
        return tableData;
    }

    @Transactional(propagation = Propagation.REQUIRED) // 事务控制
    @Override
    public List<String> uploadFile(MultipartFile file, String tableName, String type, String user, int userId, String parentId, String parentType, String status) throws IOException, ParseException {
        // 封住表描述信息
        TableDescribeEntity tableDescribeEntity = new TableDescribeEntity();
        tableDescribeEntity.setClassPath(parentType+"/"+type);
        // 解析系统当前时间
        tableDescribeEntity.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        tableDescribeEntity.setCreateUser(user);
        tableDescribeEntity.setTableName(tableName);
        // 封装目录信息
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setLabel(tableName);
        CategoryEntity parentCate = categoryMapper.selectById(parentId);
        categoryEntity.setStatus(status);
        categoryEntity.setCatLevel(parentCate.getCatLevel()+1);
        categoryEntity.setIsCommon(parentCate.getIsCommon());
        categoryEntity.setIsLeafs(1);
        categoryEntity.setUid(UserThreadLocal.get().getUid().toString());
        categoryEntity.setPath(parentCate.getPath()+"/"+tableName);
        categoryEntity.setParentId(parentId);
        categoryEntity.setIsDelete(0);


        // 保存数据库
        categoryMapper.insert(categoryEntity);
        tableDescribeEntity.setTableId(categoryEntity.getId());
        tableDescribeMapper.insert(tableDescribeEntity);

        List<String> featureList = storeTableData(file, tableName);

        UserLog userLog = new UserLog(null, UserThreadLocal.get().getUid(),UserThreadLocal.get().getUsername(),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),"数据文件上传");
        userLogService.save(userLog);

        statisticalDataMapper.deleteCache(); // 清除缓存

        return featureList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void createTable(String tableName, List<CreateTableFeatureVo> characterList, String createUser, CategoryEntity nodeData) {
        /**
         *          筛选数据
         *              查询当前目录下的宽表所有数据
         *                  1、查询nodeData的所有子节点，找到是宽表的节点信息（表名）
         *              筛选 其他病种符合条件的所有数据
         *                  2、遍历所有目录信息 找到所有的宽表节点，排除上一步找到的宽表节点，使用剩下的宽表节点筛选数据
         *              合并所有数据
         *          创建表头信息
         *              3、根据宽表的字段管理表创建一个新表，存储筛选后的数据
         *          保存创建表的数据信息信息
         *              4、将1、2步骤的数据插入到3创建的表中
         *          保存目录信息
         *              5、创建目录节点信息，并保存数据库
         *
         */
        List<LinkedHashMap<String, Object>> res = getFilterDataByConditions(characterList, nodeData);
        CategoryEntity mustContainNode = getBelongType(nodeData, new ArrayList<CategoryEntity>());
        // 查询考虑疾病的宽表数据
        List<LinkedHashMap<String,Object>> diseaseData;
         if(mustContainNode!=null) {
             diseaseData = tableDataMapper.getAllTableData(mustContainNode.getLabel()); // 传递表名参数
         }else{
             diseaseData = new ArrayList<>();
        }
        // 合并考虑疾病和非考虑疾病的所有数据
        for (LinkedHashMap<String, Object> re : res) {
            diseaseData.add(re);
        }
        // 创建表头信息 获取宽表字段管理信息
        List<FieldManagementEntity> fields = fieldManagementService.list(null);
        HashMap<String, String> fieldMap = new HashMap<>();
        for (FieldManagementEntity field : fields) {
            fieldMap.put(field.getFeatureName(),field.getType());
        }
        // TODO 创建表头信息
        tableDataMapper.createTableByField(tableName,fieldMap);
        // TODO 数据保存 批量插入
        // TODO 保证value值数量与字段个数一致
        for (Map<String, Object> diseaseDatum : diseaseData) {
            for (FieldManagementEntity field : fields) {
                if(diseaseDatum.get(field.getFeatureName())==null)
                {
                    diseaseDatum.put(field.getFeatureName(),null);
                }
            }
        }
        // TODO 分批插入 防止sql参数传入过多导致溢出
        if(diseaseData.size()>200){
            int batch = diseaseData.size()/200;
            for(int i=0; i<batch; i++){
                int start = i*200, end = (i+1)*200;
                tableDataMapper.bachInsertData(diseaseData.subList(start,end),tableName); // diseaseData.subList(start,end) 前闭后开
            }
            tableDataMapper.bachInsertData(diseaseData.subList(batch*200,diseaseData.size()),tableName);
        }else{
            tableDataMapper.bachInsertData(diseaseData,tableName);
        }
        // 目录信息
        CategoryEntity node = new CategoryEntity();
        node.setIsDelete(0);
        node.setParentId(nodeData.getId());
        node.setPath(nodeData.getPath()+"/"+tableName);
        node.setIsLeafs(1);
        node.setIsCommon(nodeData.getIsCommon());
        node.setCatLevel(nodeData.getCatLevel()+1);
        node.setIsWideTable(0);
        node.setLabel(tableName);
        node.setStatus(nodeData.getStatus());
        node.setUid(UserThreadLocal.get().getUid().toString());
        categoryMapper.insert(node); // 保存目录信息
        // 表描述信息
        TableDescribeEntity tableDescribeEntity = new TableDescribeEntity();
        tableDescribeEntity.setTableName(tableName);
        tableDescribeEntity.setCreateUser(createUser);
        tableDescribeEntity.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        tableDescribeEntity.setClassPath(nodeData.getPath()+"/"+tableName);
        tableDescribeEntity.setTableId(node.getId());
        // 保存表描述信息
        tableDescribeMapper.insert(tableDescribeEntity);
        UserLog userLog = new UserLog(null, UserThreadLocal.get().getUid(),UserThreadLocal.get().getUsername(),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),"筛选数据建表");
        userLogService.save(userLog);


        // 插入 filter_data_info 表信息
        FilterDataInfo filterDataInfo = new FilterDataInfo();
        filterDataInfo.setUid(UserThreadLocal.get().getUid());
        filterDataInfo.setCreateUser(UserThreadLocal.get().getUsername());
        filterDataInfo.setUsername(UserThreadLocal.get().getUsername());
        filterDataInfo.setCateId(nodeData.getId());
        filterDataInfo.setParentId(nodeData.getParentId());
        filterDataInfo.setFilterTime(new Timestamp(System.currentTimeMillis())); // 时间
        filterDataInfoMapper.insert(filterDataInfo);


        for (CreateTableFeatureVo createTableFeatureVo : characterList) {
            FilterDataCol filterDataCol = new FilterDataCol();
            BeanUtils.copyProperties(createTableFeatureVo,filterDataCol);
            filterDataCol.setFilterDataInfoId(filterDataInfo.getId());
            FieldManagementEntity fieldManagementEntity = fieldManagementService.getOne(new QueryWrapper<FieldManagementEntity>().eq("feature_name", createTableFeatureVo.getFeatureName()));
            filterDataCol.setRange(fieldManagementEntity.getRange());
            filterDataColMapper.insert(filterDataCol);
        }

        statisticalDataMapper.deleteCache(); // 清除数据统计的缓存

    }

    // 根据条件筛选数据
    @Transactional
    @Override
    public List<LinkedHashMap<String, Object>> getFilterDataByConditions(List<CreateTableFeatureVo> characterList,CategoryEntity nodeData) {
        List<CategoryEntity> categoryEntities = categoryMapper.selectList(null); // 查询所有的目录信息
        // 找到所有的宽表节点
        List<CategoryEntity> allWideTableNodes = categoryEntities.stream().filter(categoryEntity -> {
            return categoryEntity.getIsDelete()==0 && categoryEntity.getIsLeafs()==1 && categoryEntity.getIsWideTable()!=null && categoryEntity.getIsWideTable() == 1;
        }).collect(Collectors.toList());
        // 遍历当前节点的所有叶子节点，找到这个宽表节点
        ArrayList<CategoryEntity> leafNodes = new ArrayList<>();
        getLeafNode(nodeData, leafNodes);
        /** 找到所有的非考虑疾病的宽表节点 **/
        List<CategoryEntity> otherWideTable = null;
        if(leafNodes!=null && leafNodes.size()>0){
            for (CategoryEntity leafNode : leafNodes) {
                if(leafNode.getIsWideTable()!=null && leafNode.getIsWideTable()==1) {
                    otherWideTable = allWideTableNodes.stream().filter(categoryEntity -> { // 所有的非考虑疾病的宽表节点
                        return !categoryEntity.getLabel().equals(leafNode.getLabel());
                    }).collect(Collectors.toList());
                }
            }
        }else{
            otherWideTable = allWideTableNodes;
        }
        if(otherWideTable==null) otherWideTable = allWideTableNodes;
        // 筛选所有非考虑疾病的宽表数据
        /** select * from ${tableName} where ${feature} ${computeOpt} ${value} ${connector} ... **/
        // 前端传过来的 AND OR NOT 是数字形式0,1,2，需要变成字符串拼接sql
        for (CreateTableFeatureVo createTableFeatureVo : characterList) {
            if(createTableFeatureVo.getOpt()==null) createTableFeatureVo.setOptString("");
            else if(createTableFeatureVo.getOpt()==0) createTableFeatureVo.setOptString("AND");
            else if(createTableFeatureVo.getOpt()==1) createTableFeatureVo.setOptString("OR");
            else createTableFeatureVo.setOptString("AND NOT");
        }
        // 处理varchar类型的数据
        for (CreateTableFeatureVo createTableFeatureVo : characterList) {
            if(createTableFeatureVo.getType()==null || createTableFeatureVo.getType().equals("character varying")){
                createTableFeatureVo.setValue("'"+createTableFeatureVo.getValue()+"'");
            }
        }
        // 依次查询每一个表中符合条件的数据
        List<List<LinkedHashMap<String, Object>>> otherWideTableData = new ArrayList<>();
        for (CategoryEntity wideTable : otherWideTable) {
            otherWideTableData.add(tableDataMapper.getFilterData(wideTable.getLabel(),characterList)); // TODO 筛选SQl xml
        }
        /** 数据合并List<List<Map<String, Object>>> otherWideTableData(多张表) 合并到 List<Map<String,Object>> diseaseData（一张表） **/
        ArrayList<LinkedHashMap<String, Object>> res = new ArrayList<>();
        for (List<LinkedHashMap<String, Object>> otherWideTableDatum : otherWideTableData) {
            for (LinkedHashMap<String, Object> rowData : otherWideTableDatum) {
                res.add(rowData);
            }
        }

//        // 插入 filter_data_info 表信息
//        FilterDataInfo filterDataInfo = new FilterDataInfo();
//        filterDataInfo.setUid(UserThreadLocal.get().getUid());
//        filterDataInfo.setCreateUser(UserThreadLocal.get().getUsername());
//        filterDataInfo.setUsername(UserThreadLocal.get().getUsername());
//        filterDataInfo.setCateId(nodeData.getId());
//        filterDataInfo.setParentId(nodeData.getParentId());
//        filterDataInfo.setFilterTime(new Timestamp(System.currentTimeMillis())); // 时间
//        filterDataInfoMapper.insert(filterDataInfo);
//
//
//        ArrayList<FilterDataCol> filterDataCols = new ArrayList<>();
//        for (CreateTableFeatureVo createTableFeatureVo : characterList) {
//            FilterDataCol filterDataCol = new FilterDataCol();
//            BeanUtils.copyProperties(createTableFeatureVo,filterDataCol);
//            filterDataCol.setFilterDataInfoId(filterDataInfo.getId());
//            FieldManagementEntity fieldManagementEntity = fieldManagementService.getOne(new QueryWrapper<FieldManagementEntity>().eq("feature_name", createTableFeatureVo.getFeatureName()));
//            filterDataCol.setRange(fieldManagementEntity.getRange());
//            filterDataColMapper.insert(filterDataCol);
//        }


        UserLog userLog = new UserLog(null, UserThreadLocal.get().getUid(),UserThreadLocal.get().getUsername(),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),"筛选数据");
        userLogService.save(userLog);
        return res;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<String> storeTableData(MultipartFile file, String tableName) throws IOException {
        ArrayList<String> featureList = null;
        if (!file.isEmpty()) {
            // 使用 OpenCSV 解析 CSV 文件
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(),"UTF-8"));
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> csvData = csvReader.readAll();
            csvReader.close();
            // 获取表头信息
            String[] headers = csvData.get(0);
            featureList = new ArrayList<String>(Arrays.asList(headers));
            // 删除表头行，剩余的即为数据行
            csvData.remove(0);
            // 解析字段类型
            String[] featureDataType = getFeatureDataType(csvData);
            // 创建表信息
            tableDataMapper.createTable2(headers,featureDataType, tableName);

//            // 批量
            // TODO 分批插入 防止sql参数传入过多导致溢出
            if(csvData.size()>10000){
                int batch = csvData.size()/10000;
                for(int i=0; i<batch; i++){
                    int start = i*10000, end = (i+1)*10000;
                    tableDataMapper.batchInsertCsv(csvData.subList(start,end),tableName); // diseaseData.subList(start,end) 前闭后开
                }
                tableDataMapper.batchInsertCsv(csvData.subList(batch*10000,csvData.size()),tableName);
            }else{
                tableDataMapper.batchInsertCsv(csvData,tableName);
            }
            // TODO 改进
//            int count = csvData.size();
//            int pageSize = 5000;
//            int threadNum = count % pageSize == 0 ?  count / pageSize:  count / pageSize + 1;
//            CountDownLatch downLatch = new CountDownLatch(threadNum);
//            long start = System.currentTimeMillis();
//
//            for (int i = 0; i < threadNum; i++) {
//                //开始序号
//                int startIndex = i * pageSize;
//                //结束序号
//                int endIndex = Math.min(count, (i+1)*pageSize);
//                //分割list
//                List<String[]> epochData = csvData.subList(startIndex, endIndex);
//
//                taskExecutor.execute(() -> {
//                    try {
//                        tableDataMapper.batchInsertCsv(epochData,tableName);
//
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }finally {
//                        //执行完后 计数
//                        downLatch.countDown(); // 计数器减1
//                    }
//                });
//            }
//            try {
//                //等待
//                downLatch.await(); //等待所有的线程执行结束 当里面的数值变为0的时候主线程就不在等待
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            long end = System.currentTimeMillis();
//            System.out.println("插入数据耗费时间："+(end-start));

        }
        return featureList;
    }

    private String[] getFeatureDataType(List<String[]> csvData) {
        String[] dataType = new String[csvData.get(0).length];
        for (String[] csvDatum : csvData) {  //遍历每一行
            for (int i=0; i<csvDatum.length; i++) { // 遍历每一列
                try {
                    Float.parseFloat(csvDatum[i]); // 可以转成数字
                }catch (Exception e){ // 不可以转成数字
                    dataType[i] = "VARCHAR(255)";
                }
            }
        }
        for(int i=0; i<csvData.get(0).length; i++){
            if(dataType[i]==null) dataType[i]="float8";
        }
        System.out.println("字段类型为："+JSON.toJSONString(dataType));
        return dataType;
    }

    // 数据填充后将数据导出csv 文件 List<String> 每一个string都是一行数据，以逗号分开
    @Override
    public List<String> exportFile(ExportFilledDataTableVo dataFillMethodVo){
        // 获取填充的数据信息
        List<Map<String, IsFillVo>> fillData = indicatorManagementService.fillData(dataFillMethodVo.getDataFillMethodVo()); // 每一个map是一行数据（只有填充的列）
        // 获取这个表的所有数据
        List<LinkedHashMap<String, Object>> tableData = tableDataMapper.getAllTableData(dataFillMethodVo.getDataFillMethodVo().getTableName());
        // 获取表头信息
        List<FieldManagementEntity> fields = fieldManagementService.list();
        // 数据整合 表的所有数据有空值，使用填充后的数据替换
        for(int i=0; i<tableData.size(); i++){
            for (IndicatorsMissDataVo colNameVos : dataFillMethodVo.getDataFillMethodVo().getMissCompleteMethod()) {
                if(!tableData.get(i).containsKey(colNameVos.getIndex())){ // 不包含这个列就说明这个是空
                    tableData.get(i).put(colNameVos.getIndex(),fillData.get(i).get(colNameVos.getIndex()).getValue());
                }
            }
        }
        // 每行map长度补齐
        for (LinkedHashMap<String, Object> row : tableData) {
            for (FieldManagementEntity field : fields) {
                if(!row.containsKey(field.getFeatureName())){
                    row.put(field.getFeatureName(),"");
                }
            }
        }
        List<String> fileData = new ArrayList<>();
        StringBuffer tableHead = new StringBuffer();
        for(int i=0; i<fields.size(); i++){
            if(i!=fields.size()-1){
                tableHead.append(fields.get(i).getFeatureName()+",");
            }else{
                tableHead.append(fields.get(i).getFeatureName());
            }
        }
        fileData.add(tableHead.toString());
        for (LinkedHashMap<String, Object> rowData: tableData) {
            StringBuffer temp = new StringBuffer();
            for(int i=0; i<fields.size(); i++){
                if(i!=fields.size()-1) {
                    temp.append(rowData.get(fields.get(i).getFeatureName())+",");
                }else{
                    temp.append(rowData.get(fields.get(i).getFeatureName()));
                }
            }
            fileData.add(temp.toString());
        }
        UserLog userLog = new UserLog(null, UserThreadLocal.get().getUid(),UserThreadLocal.get().getUsername(),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),"疾病数据文件导出");
        userLogService.save(userLog);
        return fileData;
    }


    // 对某个特征进行描述性分析
    @Override
    public FeatureDescAnaVo featureDescAnalyze(String featureName, String tableName,CreateTaskEntity taskInfo) throws IOException, URISyntaxException {
        // 判断这个特征属于离散还是非离散
        FeatureDescAnaVo featureDescAnaVo = new FeatureDescAnaVo();
        FieldManagementEntity featureEntity = fieldManagementService.getOne(new QueryWrapper<FieldManagementEntity>().eq("feature_name", featureName));
        if(featureEntity==null){ // 不被字段管理表管理
            featureEntity = new FieldManagementEntity();
            // 查询数据库判断是否离散
            Map<String, Long> map= indicatorManagementMapper.getFiledCount(featureName, tableName);
            if(map.get("num1")<=10 && map.get("num1")>=1 && 1.0*map.get("num1")/map.get("num2")<0.05) {
                System.out.println("判断为离散");
                featureEntity.setDiscrete(true);
            }

            else featureEntity.setDiscrete(false);
        }
        if(featureEntity.getDiscrete()==null || !featureEntity.getDiscrete()){ // 非离散
            // 获取 总条数 中位数 众数 均值 中位数 最小值 最大值 众数
            // 调用远程方法
            Integer totalCount = tableDataMapper.getAllCount(tableName);
            ArrayList<String> cols = new ArrayList<>();
            cols.add(featureName);
            RunPyEntity param = new RunPyEntity(tableName,null,cols);
            JsonNode jsonNode = HTTPUtils.postRequest(param, "/notDiscreteFeatureDesc");
            // 解析返回值
            NotDiscrete notDiscrete = getDataFromPy(jsonNode);
            /**  数据分段 柱状图数据 6个柱子 一个柱子表示一个数据范围取值的数据量 跟据最大最小值划分区间 判断类型 如果是整数就转换成整数就不保留小数， 浮点数就保留小数 **/
            LinkedHashMap<String, Integer> binData = getBinData(notDiscrete.getMax(), notDiscrete.getMin(), tableName, featureName);

            notDiscrete.setBinData(binData);
            notDiscrete.setTotal(totalCount);
            featureDescAnaVo.setNotDiscrete(notDiscrete);
            featureDescAnaVo.setDiscrete(false);
        }else{ // 离散
            List<String> values = new ArrayList<>();
            if(featureEntity.getRange() == null) { // 不被字段管理表管理
                // 获取表中这个字段有哪些不同的取值
                System.out.println("表名："+tableName +"  列名："+featureName+ "  mapper:"+tableDataMapper);
                Object[] res =  tableDataMapper.getDistinctValue(tableName, featureName);
                System.out.println("返回结果"+JSON.toJSONString(res));
                for (Object re : res) values.add(re.toString());
            }else{
                String s = featureEntity.getRange().replace("{", "").replace("}", "");
                String[] featureValues = s.split(",");
                for (String featureValue : featureValues) values.add(featureValue);
            }
            ArrayList<DiscreteVo> discreteVos = new ArrayList<>();
            // 获取数据总条数

            Integer totalCount = tableDataMapper.getAllCount(tableName);
            Integer validCount = 0;
            for (String featureValue : values) {
                DiscreteVo discreteVo = new DiscreteVo();
                // 计算每个取值的数据总条数
                featureValue = featureValue.trim().replace("\"", "");
                System.out.println("featureValue = "+featureValue);
                Integer count = tableDataMapper.getCount(featureValue,tableName,featureName);
                validCount+=count;
                discreteVo.setFrequent(count);
                discreteVo.setTotalAmount(Float.parseFloat(String.format("%.2f",(float)count/totalCount*100))); // 累计占比
                discreteVo.setVariable(featureValue);
                discreteVos.add(discreteVo);
                System.err.println("当前离散vo:"+discreteVo);
            }
            for (DiscreteVo discreteVo : discreteVos) {
                discreteVo.setAmount(Float.parseFloat(String.format("%.2f",(float)discreteVo.getFrequent()/validCount*100))); // 有效值占比
            }
            featureDescAnaVo.setDiscrete(true);
            featureDescAnaVo.setDiscreteVos(discreteVos);

        }
        UserLog userLog = new UserLog(null, UserThreadLocal.get().getUid(),UserThreadLocal.get().getUsername(),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),"疾病数据描述性分析");
        userLogService.save(userLog);


        //判断任务管理中是否有该任务，如果有就不在继续创建 没有就创建任务
        List<Task> list = taskService.list(null);
        List<Task> isRepeat = list.stream().filter(task -> {
            return "描述性分析".equals(task.getTasktype()) && task.getFeature().equals(featureName) && task.getDataset().equals(tableName) && task.getTaskname().equals(taskInfo.getTaskName());
        }).collect(Collectors.toList());
        if(isRepeat == null || isRepeat.size() == 0){
            Task task = new Task();
            task.setCreatetime(new Timestamp(System.currentTimeMillis()));
            task.setDataset(tableName);
            task.setFeature(featureName);

            String leader = taskInfo.getPrincipal();
            if(leader == null || leader=="") {
                System.out.println("操作人为空");
                leader = UserThreadLocal.get().getUsername();
            }
            task.setLeader(leader);

            String taskName = taskInfo.getTaskName();
            if(taskName == null || taskName=="") {
                System.out.println("任务名称为空");
                taskName = UserThreadLocal.get().getUsername()+"_"+"描述性分析_"+ LocalDate.now().toString();
            }
            task.setTaskname(taskName);
            task.setParticipant(taskInfo.getParticipants());
            task.setTasktype(taskInfo.getTasktype()==null?"描述性分析":taskInfo.getTasktype());

            // 获取关联疾病 TODO
            // 跟据表名获取父节点的名称 select label from category where "id"=(select parent_id from category where label='copd')
            String label = categoryMapper.getParentLabelByLabel(tableName);
            task.setDisease(label);
            task.setRemark(taskInfo.getTips());

            task.setResult(JSON.toJSONString(featureDescAnaVo));
            task.setUserid(UserThreadLocal.get().getUid());
            task.setTargetcolumn(featureName);
            System.out.println("插入："+task);
            taskService.save(task);
        }
        return featureDescAnaVo;
    }

    @Override
    public SingleAnalyzeVo singleFactorAnalyze(String tableName, List<String> colNames,CreateTaskEntity taskInfo) throws IOException, URISyntaxException {
        RunPyEntity param = new RunPyEntity(tableName,null,colNames);
        JsonNode jsonNode = HTTPUtils.postRequest(param, "/singleFactorAnalyze");
        SingleAnalyzeVo singleAnalyzeDataFromJsonNode = getSingleAnalyzeDataFromJsonNode(jsonNode);

        List<Task> list = taskService.list(null);
        List<Task> isRepeat = list.stream().filter(task -> {
            return task.getTasktype().equals("单因素分析") && task.getFeature().equals(colNames.stream().collect(Collectors.joining(","))) && task.getDataset().equals(tableName) && task.getTaskname().equals(taskInfo.getTaskName());
        }).collect(Collectors.toList());
        if(isRepeat == null || isRepeat.size() == 0){
            // 创建任务
            System.err.println("开始创建任务");
            Task task = new Task();
            task.setCreatetime(new Timestamp(System.currentTimeMillis()));
            task.setDataset(tableName);
            String featureNames = colNames.stream().collect(Collectors.joining(","));
            task.setFeature(featureNames);

            String leader = taskInfo.getPrincipal();
            if(leader == null || leader=="") {
                leader = UserThreadLocal.get().getUsername();
            }
            task.setLeader(leader);
            task.setRemark(taskInfo.getTips());
            String taskName = taskInfo.getTaskName();
            if(taskName == null || taskName=="") taskName = UserThreadLocal.get().getUsername()+"_"+"单因素分析_"+ LocalDate.now().toString();
            task.setTaskname(taskName);
            task.setParticipant(taskInfo.getParticipants());
            task.setTasktype(taskInfo.getTasktype()==null?"单因素分析":taskInfo.getTasktype());

            // 获取关联疾病 TODO
            // 跟据表名获取父节点的名称 select label from category where "id"=(select parent_id from category where label='copd')
            String label = categoryMapper.getParentLabelByLabel(tableName);
            task.setDisease(label);
            task.setResult(JSON.toJSONString(singleAnalyzeDataFromJsonNode));
            task.setUserid(UserThreadLocal.get().getUid());
            task.setTargetcolumn(colNames.get(1));
            taskService.save(task);
        }
        return singleAnalyzeDataFromJsonNode;
    }

    // TODO 一致性验证
    @Override
    public ConsistencyAnalyzeVo consistencyAnalyze(String tableName, String featureName,CreateTaskEntity taskInfo) throws IOException, URISyntaxException {
        ArrayList<String> featureNames = new ArrayList<>();
        featureNames.add(featureName);
        RunPyEntity param = new RunPyEntity(tableName,null,featureNames);
        JsonNode jsonNode = HTTPUtils.postRequest(param, "/consistencyAnalyze");
        System.out.println("返回状态码："+jsonNode.get("code").asInt());
        if(jsonNode.get("code").asInt()==500) return null;
        List<ICCVo> consistencyAnalyzeFromJsonNode = getConsistencyAnalyzeFromJsonNode(jsonNode);
        ConsistencyAnalyzeVo consistencyAnalyzeVo = new ConsistencyAnalyzeVo();
        consistencyAnalyzeVo.setICCAnalyzeResult(consistencyAnalyzeFromJsonNode);

        List<Task> list = taskService.list(null);
        List<Task> isRepeat = list.stream().filter(task -> {
            return task.getTasktype().equals("一致性验证") && task.getFeature().equals(featureName) && task.getDataset().equals(tableName)&&task.getTaskname().equals(taskInfo.getTaskName());
        }).collect(Collectors.toList());
        if(isRepeat == null || isRepeat.size() == 0) {
            // 创建任务
            Task task = new Task();
            task.setCreatetime(new Timestamp(System.currentTimeMillis()));
            task.setDataset(tableName);
            task.setFeature(featureName);

            String leader = taskInfo.getPrincipal();
            if(leader == null || leader=="") {
                leader = UserThreadLocal.get().getUsername();
            }
            task.setLeader(leader);
            task.setRemark(taskInfo.getTips());
            String taskName = taskInfo.getTaskName();
            if(taskName == null || taskName=="") taskName = UserThreadLocal.get().getUsername()+"_"+"一致性验证_"+ LocalDate.now().toString();
            task.setTaskname(taskName);
            task.setParticipant(taskInfo.getParticipants());
            task.setTasktype(taskInfo.getTasktype()==null?"一致性验证":taskInfo.getTasktype());

            // 获取关联疾病 TODO
            // 跟据表名获取父节点的名称 select label from category where "id"=(select parent_id from category where label='copd')
            String label = categoryMapper.getParentLabelByLabel(tableName);
            task.setDisease(label);
            task.setModel("ICC");
            task.setResult(JSON.toJSONString(consistencyAnalyzeVo));
            task.setUserid(UserThreadLocal.get().getUid());
            task.setTargetcolumn(featureName);
            System.err.println("保存任务信息："+task);
            taskService.save(task);
        }
        return consistencyAnalyzeVo;
    }

    @Override
    public List<Map<String, Object>> getTableDataByFields(String tableName, List<String> featureList) {
        List<Map<String, Object>> tableData = tableDataMapper.getTableDataByFields(tableName,featureList);
        return tableData;
    }

    @Override
    public Integer getDataCount(List<CategoryEntity> categoryEntities) {
        int count = 0;
        for (CategoryEntity categoryEntity : categoryEntities) {
            count+=tableDataMapper.getCountByName(categoryEntity.getLabel());
        }
        return count;
    }

    private List<ICCVo> getConsistencyAnalyzeFromJsonNode(JsonNode jsonNode){
        JsonNode icc1 = jsonNode.get("ICC1");
        JsonNode icc2 = jsonNode.get("ICC2");
        ICCVo iccVo1 = new ICCVo();
        ICCVo iccVo2 = new ICCVo();
        iccVo1.setMethod(icc1.get("method").asText());
        iccVo1.setType(icc1.get("type").asText());
        iccVo1.setICC(icc1.get("ICC").asDouble());
        iccVo1.setDf1(icc1.get("df1").asInt());
        iccVo1.setDf2(icc1.get("df2").asInt());
        iccVo1.setF(icc1.get("F").asDouble());
        iccVo1.setP(icc1.get("p").asDouble());

        iccVo2.setMethod(icc2.get("method").asText());
        iccVo2.setType(icc2.get("type").asText());
        iccVo2.setICC(icc2.get("ICC").asDouble());
        iccVo2.setDf1(icc2.get("df1").asInt());
        iccVo2.setDf2(icc2.get("df2").asInt());
        iccVo2.setF(icc2.get("F").asDouble());
        iccVo2.setP(icc2.get("p").asDouble());
        ArrayList<ICCVo> iccVos = new ArrayList<>();
        iccVos.add(iccVo1);
        iccVos.add(iccVo2);
        System.out.println("ICC:"+JSON.toJSONString(iccVos));
        return iccVos;
    }

    private SingleAnalyzeVo getSingleAnalyzeDataFromJsonNode(JsonNode jsonNode){
        SingleAnalyzeVo singleAnalyzeVo = new SingleAnalyzeVo();
        singleAnalyzeVo.setWilcoxonW(jsonNode.get("w_stat").asDouble());
        singleAnalyzeVo.setWilcoxonP(jsonNode.get("p_value_w").asDouble());
        singleAnalyzeVo.setTT(jsonNode.get("t_stat").asDouble());
        singleAnalyzeVo.setTP(jsonNode.get("p_value_t").asDouble());
        singleAnalyzeVo.setCorrectTT(jsonNode.get("correct_t_stat").asDouble());
        singleAnalyzeVo.setCorrectTP(jsonNode.get("correct_p_value_t").asDouble());

        JsonNode classInfo = jsonNode.get("classInfo"); // 字典对象list。包含了每个类别的统计信息类别名称（className）类别总数 有效个数 缺失值个数
        System.out.println("classInfo:"+JSON.toJSONString(classInfo));
        ArrayList<DiscreteVo> discreteVos = new ArrayList<>();
        for (JsonNode node : classInfo) {
            DiscreteVo discreteVo = new DiscreteVo();
            discreteVo.setVariable(node.get("className").asText());
            discreteVo.setFrequent(node.get("frequent").asInt());
            discreteVo.setValidFrequent(node.get("validFrequent").asInt());
            discreteVo.setMissFrequent(node.get("missFrequent").asInt());
            discreteVos.add(discreteVo);
        }
        singleAnalyzeVo.setDiscreteVos(discreteVos);
        // 分类获取两组数据
        ArrayList<Double> group1 = new ArrayList<>();
        JsonNode group1Node = jsonNode.get("group1");
        for (JsonNode node : group1Node) {
            group1.add(node.asDouble());
        }
        ArrayList<Double> group2 = new ArrayList<>();
        JsonNode group2Node = jsonNode.get("group2");
        for (JsonNode node : group2Node) {
            group2.add(node.asDouble());
        }
        // 分成六个柱状图的柱子描述数据分布区间
        NotDiscrete notDiscrete1 = new NotDiscrete();
        NotDiscrete notDiscrete2 = new NotDiscrete();
        if(group1.size()>0) notDiscrete1.setBinData(getBinData(group1,7));
        singleAnalyzeVo.setNotDiscrete(notDiscrete1);
        if(group2.size()>0) notDiscrete2.setBinData(getBinData(group2,7));
        singleAnalyzeVo.setNotDiscrete2(notDiscrete2);
        return singleAnalyzeVo;
    }


    private LinkedHashMap<String,Integer> getBinData(List<Double> data,int binCount){
        LinkedHashMap<String, Integer> res = new LinkedHashMap<>();
        data.sort(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                if(o1>o2) return 1;
                else if(o1<o2) return -1;
                else return 0;
            }
        });
        // 找到最大值 最小值 分区间划分
        double max = data.get(data.size()-1);
        double min = data.get(0);
        double gap = (max-min)/binCount;

        for(int i=0; i<binCount; i++){
            int count = 0;
            Double end,start;

            start = min + gap * i;
            if(i==binCount-1) end = max;
            else end = min + gap * (i+1);
            for (Double datum : data) {
                if(i==binCount-1){
                    if (datum < end && datum >= start) count++;
                }else{
                    if (datum <= end && datum >= start) count++;
                }

            }
            start = Double.parseDouble(String.format("%.2f", start));
            end = Double.parseDouble(String.format("%.2f", end));
            res.put(start+"~"+end,count);
        }
        return res;
    }




    private boolean writeDataToCSV(List<String> data, String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // 写入数据
            for (String line : data) {
                String[] parts = line.split(","); // 分割每行的数据
                writer.writeNext(parts); // 写入CSV文件
            }
            return true;
        } catch (IOException e) {
            System.err.println("写入CSV文件时出现错误: " + e.getMessage());
            return false;
        }
    }

    private NotDiscrete getDataFromPy(JsonNode jsonNode){
        NotDiscrete notDiscrete = new NotDiscrete();
        String averageAsString = jsonNode.get("average").asText();
        float average = Float.parseFloat(averageAsString);
        notDiscrete.setAverage(Float.parseFloat(String.format("%.2f", average)));
        notDiscrete.setMax(Float.parseFloat(jsonNode.get("max").asText()));
        notDiscrete.setMin(Float.parseFloat(jsonNode.get("min").asText()));
        notDiscrete.setMode(Float.parseFloat(jsonNode.get("mode").asText()));
        notDiscrete.setMiddle(Float.parseFloat(jsonNode.get("middle").asText()));
        notDiscrete.setTotal(jsonNode.get("middle").asInt());
        return notDiscrete;
    }
    private LinkedHashMap<String,Integer> getBinData(Float max, Float min, String tableName, String featureName){
        // 分成6个区间
        float gap = (max-min)/6;
        gap = Float.parseFloat(String.format("%.2f", gap));
        LinkedHashMap<String, Integer> binData = new LinkedHashMap<>();
        float start = min;
        float end = min;
        for(int i=0; i<6; i++){
            start = Float.parseFloat(String.format("%.2f",min+gap*i));
            if(i==5) end = max;
            else {
                end = Float.parseFloat(String.format("%.2f",min+gap*(i+1)));
            }

            // 获取每个区间的数据量
            String type = null;
            FieldManagementEntity fieldManagementEntity = fieldManagementService.getOne(new QueryWrapper<FieldManagementEntity>().eq("feature_name", featureName));
            if(fieldManagementEntity==null) type =  indicatorManagementMapper.getColType(tableName,featureName);
            else type = fieldManagementEntity.getType();
            if ("integer".equals(type)){
                int s = (int)start;
                int e = (int)end;
                Integer count = tableDataMapper.getCountByCondition(String.valueOf(s),String.valueOf(e), tableName,featureName);
                binData.put(start+"~"+end,count);
            }else{
                Integer count = tableDataMapper.getCountByCondition(String.valueOf(start),String.valueOf(end), tableName,featureName);
                binData.put(start+"~"+end,count);
            }

        }
        return binData;
    }

    private void getLeafNode(CategoryEntity nodeData,List<CategoryEntity> leafNodes){
        if(nodeData.getChildren()!=null && nodeData.getChildren().size()>0){
            for (CategoryEntity child : nodeData.getChildren()) {
                if(child.getIsLeafs()!=null && child.getIsLeafs()==1) leafNodes.add(child);
                else getLeafNode(child,leafNodes);
            }
        }

    }

    private CategoryEntity getBelongType(CategoryEntity nodeData, ArrayList<CategoryEntity> leafNodes){
        getLeafNode(nodeData, leafNodes);
        if(leafNodes!=null && leafNodes.size()>0){
            for (CategoryEntity leafNode : leafNodes) {
                if(leafNode.getIsWideTable()!=null && leafNode.getIsWideTable()==1) {
                    return leafNode;
                }
            }
        }
        return null;
    }




    @Override
    public Integer getCountByTableName(String tableName) {
        return tableDataMapper.getCountByTableName(tableName);
    }

    @Override
    public List<String> getTableFields(String tableName) {
        return tableDataMapper.getTableFields(tableName);
    }

    @Override
    public List<LinkedHashMap<String, Object>> getDataLikeMatch(String tableName, List<String> tableFields, String value) {
        return tableDataMapper.getDataByLikeMatch(tableName, tableFields, value);
    }

//    @Override
//    public void createFilterBtnTable(String dataName, List<CreateTableFeatureVo> characterList, String createUser, String status, String uid, String username, String isFilter, String isUpload, String uidList, String nodeid) {
//
//    }

    @Override
    public void createFilterBtnTable(String tableName, List<CreateTableFeatureVo> characterList, String createUser, String status, String uid, String username, String IsFilter, String IsUpload, String uid_list,String nodeid) {
        /**
         *          筛选数据
         *              查询当前目录下的宽表所有数据
         *                  1、查询nodeData的所有子节点，找到是宽表的节点信息（表名）
         *              筛选 其他病种符合条件的所有数据
         *                  2、遍历所有目录信息 找到所有的宽表节点，排除上一步找到的宽表节点，使用剩下的宽表节点筛选数据
         *              合并所有数据
         *          创建表头信息
         *              3、根据宽表的字段管理表创建一个新表，存储筛选后的数据
         *          保存创建表的数据信息信息
         *              4、将1、2步骤的数据插入到3创建的表中
         *          保存目录信息
         *              5、创建目录节点信息，并保存数据库
         *
         */

        System.out.println("前端传递的值："+"表名"+tableName+"  userName"+username+"  userId"+uid+"  IsFilter"+IsFilter+"  IsUpload"+"  uid_list"+uid_list+"  nodeID"+nodeid);

        CategoryEntity nodeData = categoryMapper.selectById(nodeid);
        List<LinkedHashMap<String, Object>> res = getFilterDataByConditionsByDieaseId(characterList,uid,username,nodeid);
//        CategoryEntity mustContainNode = getBelongType(nodeData, new ArrayList<CategoryEntity>());
//        // 查询考虑疾病的宽表数据
        //List<LinkedHashMap<String,Object>> diseaseData = tableDataMapper.getAllTableData("merge"); // 传递表名参数
        List<LinkedHashMap<String,Object>> diseaseData = res;
//        System.out.println("考虑疾病所有数据");
//        // 合并考虑疾病和非考虑疾病的所有数据
//        for (LinkedHashMap<String, Object> re : res) {
//            diseaseData.add(re);
//        }
        // 创建表头信息 获取宽表字段管理信息
        List<FieldManagementEntity> fields = fieldManagementService.list(null);
        // System.out.println("字段长度为："+fields.size());
        HashMap<String, String> fieldMap = new HashMap<>();
        for (FieldManagementEntity field : fields) {
            fieldMap.put(field.getFeatureName(),field.getUnit());
        }
        // TODO 创建表头信息
        tableDataMapper.createTableByField(tableName,fieldMap);
        // TODO 数据保存 批量插入
        // TODO 保证value值数量与字段个数一致
        for (Map<String, Object> diseaseDatum : diseaseData) {
            for (FieldManagementEntity field : fields) {
                if(diseaseDatum.get(field.getFeatureName())==null)
                {
                    diseaseDatum.put(field.getFeatureName(),"");
                }
            }
//            System.out.println("数据长度为："+diseaseDatum.size());
        }
        System.out.println("========================================");
        System.out.println("数据长度："+diseaseData.size());
        // TODO 分批插入 防止sql参数传入过多导致溢出
        if(diseaseData.size()>200){
            int batch = diseaseData.size()/200;
            for(int i=0; i<batch; i++){
                int start = i*200, end = (i+1)*200;
                System.out.println("插入第"+i+"轮数据");
                tableDataMapper.bachInsertData(diseaseData.subList(start,end),tableName); // diseaseData.subList(start,end) 前闭后开
            }
            tableDataMapper.bachInsertData(diseaseData.subList(batch*200,diseaseData.size()),tableName);
        }else{
            tableDataMapper.bachInsertData(diseaseData,tableName);
        }
        // 目录信息
        CategoryEntity node = new CategoryEntity();
        node.setIsDelete(0);
        node.setParentId(nodeData.getId());
        node.setIsLeafs(1);
        node.setStatus(status);
        node.setUid(uid);
        node.setUsername(username);
        node.setCatLevel(nodeData.getCatLevel()+1);
        node.setLabel(tableName);
        node.setIs_filter(IsFilter);
        System.out.println(uid_list);
        node.setIs_upload(IsUpload);
        node.setUidList(uid_list);
        System.out.println(node);
        categoryMapper.insert(node); // 保存目录信息

        // 表描述信息
        TableDescribeEntity tableDescribeEntity = new TableDescribeEntity();
        tableDescribeEntity.setTableName(tableName);
        tableDescribeEntity.setCreateUser(node.getUsername());
//        tableDescribeEntity.setUid(Integer.parseInt(uid));
        tableDescribeEntity.setUid(UserThreadLocal.get().getUid());
        tableDescribeEntity.setTableStatus(node.getStatus());
        tableDescribeEntity.setCreateUser(username);
        tableDescribeEntity.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        tableDescribeEntity.setClassPath(nodeData.getLabel()+"/"+tableName);
        tableDescribeEntity.setTableId(node.getId());
        tableDescribeEntity.setTableSize(0.0f);
        // 保存表描述信息
        tableDescribeMapper.insert(tableDescribeEntity);

    }

    @Override
    public List<LinkedHashMap<String, Object>> getFilterDataByConditionsByDieaseId(List<CreateTableFeatureVo> characterList, String uid, String username, String nodeid) {
        System.out.println("历史数据筛选参数：nodeId "+nodeid +"  uid "+uid+"  username "+username);

        List<CategoryEntity> categoryEntities = categoryMapper.selectList(null); // 查询所有的目录信息
        CategoryEntity nodeData = categoryMapper.selectById(nodeid);
        // 找到所有的宽表节点
        List<CategoryEntity> allWideTableNodes = categoryEntities.stream().collect(Collectors.toList());
        // 遍历当前节点的所有叶子节点，找到这个宽表节点
        ArrayList<CategoryEntity> leafNodes = new ArrayList<>();
//        getLeafNode(nodeData, leafNodes);
        /** 找到所有的非考虑疾病的宽表节点 **/
        List<CategoryEntity> otherWideTable = null;
        if(leafNodes!=null && leafNodes.size()>0){
        }else{
            otherWideTable = allWideTableNodes;
        }
        if(otherWideTable==null) otherWideTable = allWideTableNodes;
        // 筛选所有非考虑疾病的宽表数据
        /** select * from ${tableName} where ${feature} ${computeOpt} ${value} ${connector} ... **/
        // 前端传过来的 AND OR NOT 是数字形式0,1,2，需要变成字符串拼接sql
        for (CreateTableFeatureVo createTableFeatureVo : characterList) {
            if(createTableFeatureVo.getOpt()==null) createTableFeatureVo.setOptString("");
            else if(createTableFeatureVo.getOpt()==0) createTableFeatureVo.setOptString("AND");
            else if(createTableFeatureVo.getOpt()==1) createTableFeatureVo.setOptString("OR");
            else createTableFeatureVo.setOptString("AND NOT");
        }
        // 处理varchar类型的数据
        for (CreateTableFeatureVo createTableFeatureVo : characterList) {
            if(createTableFeatureVo.getType()==null || createTableFeatureVo.getType().equals("character varying")){
                createTableFeatureVo.setValue("'"+createTableFeatureVo.getValue()+"'");
            }
        }
        List<List<LinkedHashMap<String, Object>>> otherWideTableData = new ArrayList<>();
        ArrayList<LinkedHashMap<String, Object>> res = new ArrayList<>();
        otherWideTableData.add(tableDataMapper.getFilterData("merge",characterList));
        ArrayList<LinkedHashMap<String, Object>> res2 = new ArrayList<>();
        for (List<LinkedHashMap<String, Object>> otherWideTableDatum : otherWideTableData) {
            for (LinkedHashMap<String, Object> rowData : otherWideTableDatum) {
                res.add(rowData);
            }
        }
        // 插入 filter_data_info 表信息
        FilterDataInfo filterDataInfo = new FilterDataInfo();
//        filterDataInfo.setUid(Integer.parseInt(uid));
        filterDataInfo.setUid(UserThreadLocal.get().getUid());
        filterDataInfo.setCreateUser(username);
        filterDataInfo.setUsername(username);
        filterDataInfo.setCateId(nodeData.getId());
        filterDataInfo.setParentId(nodeData.getParentId());
        filterDataInfo.setFilterTime(new Timestamp(System.currentTimeMillis())); // 时间
        filterDataInfoMapper.insert(filterDataInfo);


        ArrayList<FilterDataCol> filterDataCols = new ArrayList<>();
        for (CreateTableFeatureVo createTableFeatureVo : characterList) {
            FilterDataCol filterDataCol = new FilterDataCol();
            BeanUtils.copyProperties(createTableFeatureVo,filterDataCol);
            filterDataCol.setFilterDataInfoId(filterDataInfo.getId());
            FieldManagementEntity fieldManagementEntity = fieldManagementService.getOne(new QueryWrapper<FieldManagementEntity>().eq("feature_name", createTableFeatureVo.getFeatureName()));
            filterDataCol.setRange(fieldManagementEntity.getRange());
            filterDataColMapper.insert(filterDataCol);
        }
        return res;
    }


    @Override
    public List<Map<String, Object>> getInfoByTableName(String tableName) {
        return tableDataMapper.getInfoByTableName(tableName);
    }


}





