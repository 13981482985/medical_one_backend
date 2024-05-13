package com.cqupt.software_1.service.impl;

import com.alibaba.fastjson.JSON;
import com.cqupt.software_1.common.MissDataCompleteMethods;
import com.cqupt.software_1.common.RunPyEntity;
import com.cqupt.software_1.common.UserThreadLocal;
import com.cqupt.software_1.entity.FieldManagementEntity;
import com.cqupt.software_1.entity.IndicatorCategory;
import com.cqupt.software_1.entity.IndicatorManageEntity;
import com.cqupt.software_1.entity.Task;
import com.cqupt.software_1.mapper.CategoryMapper;
import com.cqupt.software_1.mapper.IndicatorManagementMapper;
import com.cqupt.software_1.mapper.TableDataMapper;
import com.cqupt.software_1.mapper.TaskMapper;
import com.cqupt.software_1.service.FieldManagementService;
import com.cqupt.software_1.service.IndicatorManagementService;
import com.cqupt.software_1.service.IndicatorService;
import com.cqupt.software_1.utils.HTTPUtils;
import com.cqupt.software_1.vo.DataFillMethodVo;
import com.cqupt.software_1.vo.IndicatorsMissDataVo;
import com.cqupt.software_1.vo.IsFillVo;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IndicatorManagementServiceImpl implements IndicatorManagementService {

    @Autowired
    IndicatorManagementMapper indicatorManagementMapper;
    @Autowired
    TableDataMapper tableDataMapper;
    @Autowired
    IndicatorService indicatorService;
    @Autowired
    FieldManagementService fieldManagementService;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    private TaskMapper taskMapper;

    // TODO 根据检查类型获取字段以及表中每一个字段的缺失率
    @Override
    public List<IndicatorManageEntity> getIndicators(List<String> types,String tableName) {
        // 判断一张表的字段是否与字段管理表对齐 不对齐就只有其他指标，对齐就可以分成人口学，社会学，生理学指标
        List<Map<String, String>> fieldMaps = indicatorManagementMapper.getTableFeilds(tableName);
        List<String> fields = fieldManagementService.list().stream().map((fieldManagementEntity -> {
            return fieldManagementEntity.getFeatureName();
        })).collect(Collectors.toList());
        HashMap<String, String> fieldMap = new HashMap<>();
        for (Map<String, String> map : fieldMaps) {
            fieldMap.put(map.get("column_name"), map.get("data_type"));
        }
        List<String> tableFields = new ArrayList<>(fieldMap.keySet());
        Collections.sort(tableFields);
        Collections.sort(fields);
        List<IndicatorManageEntity> list = new ArrayList<>();
        if(tableFields.equals(fields)){ // 这个表字段管理表所管理
            System.out.println("被字段管理表管理");
            // 跟据中文名称获取值指标类型英文名
            if(types.contains("其他")) types.remove("其他");
            if(types.size() == 0) return null;
            List<IndicatorCategory> categories = indicatorService.getEnName(types);
            List<String> indexEnNames = categories.stream().map(indicatorCategory -> {
                return indicatorCategory.getIndicator();
            }).collect(Collectors.toList());
            List<FieldManagementEntity> fieldManagementEntities = fieldManagementService.getFieldsByType(indexEnNames);
            for (FieldManagementEntity fieldManagementEntity : fieldManagementEntities) {
                IndicatorManageEntity indicatorManageEntity = new IndicatorManageEntity();
                indicatorManageEntity.setFeatureName(fieldManagementEntity.getFeatureName());
                indicatorManageEntity.setLabel(fieldManagementEntity.getChName());
                indicatorManageEntity.setDiscrete(fieldManagementEntity.getDiscrete());
                if(fieldManagementEntity.getType()!=null && !fieldManagementEntity.getType().equals("character varying")) { // 数值类型
                    if(fieldManagementEntity.getDiscrete()!=null && fieldManagementEntity.getDiscrete()) {
                        indicatorManageEntity.setFeatureDataType(2); // 数字离散
                        indicatorManageEntity.setMissCompleteMethod("前向填充");
                    }
                    else {
                        indicatorManageEntity.setFeatureDataType(1); // 数字连续
                        indicatorManageEntity.setMissCompleteMethod("均数替换");
                    }
                }else {
                    if(indicatorManageEntity.getDiscrete()!=null && indicatorManageEntity.getDiscrete()){
                        indicatorManageEntity.setFeatureDataType(3); // 文本数据离散
                        indicatorManageEntity.setMissCompleteMethod("前向填充");
                    }else continue; // 文本非离散 无法分析
                }
                if(fieldManagementEntity.getPopulation()) {
                    indicatorManageEntity.setType("diagnosis");
                    indicatorManageEntity.setTypeCh("人口学指标");
                }else if(fieldManagementEntity.getPhysiology()){
                    indicatorManageEntity.setType("vital_sign");
                    indicatorManageEntity.setTypeCh("生理学指标");
                }else if(fieldManagementEntity.getSociety()){
                    indicatorManageEntity.setType("pathology");
                    indicatorManageEntity.setTypeCh("社会学指标");
                }else{
                    indicatorManageEntity.setType("other_index");
                    indicatorManageEntity.setTypeCh("其他类型指标");
                }
                float missRate = indicatorManagementMapper.getMissRate(indicatorManageEntity.getFeatureName(),tableName); // 缺失率
                indicatorManageEntity.setMissRate(missRate);
                // 设置 range的个数 TODO
                if(fieldManagementEntity.getRange()!=null) {
                    String[] split = fieldManagementEntity.getRange().split(",");
                    indicatorManageEntity.setRangeSize(split.length);
                }else indicatorManageEntity.setRangeSize(0);
                list.add(indicatorManageEntity);
            }
            return list;
        }else{  // 没有被字段管理表所管理
            System.out.println("没有被字段管理表管管理。。");
            if(types.contains("其他")){//只有选中了 “其他”这个指标就显示没有没被字段管理的表字段信息
                // 查询字段的 类型、缺失率、离散占比（不同的数，以及对总有效数的占比）
                // 遍历每一个字段
                for (String tableField : tableFields) {
                    IndicatorManageEntity indicatorManageEntity = new IndicatorManageEntity();
                    indicatorManageEntity.setType("other"); // 设置检查类型
                    indicatorManageEntity.setFeatureName(tableField);
                    indicatorManageEntity.setLabel(tableField); // 中文名称
                    indicatorManageEntity.setTypeCh("其他");
                    //  查询 不同取值
                    Map<String, Long> map  = indicatorManagementMapper.getFiledCount(tableField, tableName);
                    long[] counts = {map.get("num1"),map.get("num2")};
                    if(counts.length==2){
                        System.out.println("开始设置值");
                        // 获取字段的连续值
                        float missRate = indicatorManagementMapper.getMissRate(tableField, tableName);
                        indicatorManageEntity.setMissRate(missRate);
                        if(counts[0]<=10 && counts[0]>=1){
                            System.out.println("aaaaaa");
                            if(1.0*counts[0]/counts[1]<0.05) { // 离散
                                indicatorManageEntity.setDiscrete(true);
                                indicatorManageEntity.setRangeSize((int)counts[0]);
                                if(fieldMap.get(tableField).equals("integer") || fieldMap.get(tableField).equals("double precision")){ // 数字类型
                                    System.out.println("数字连续");
                                    indicatorManageEntity.setFeatureDataType(2);
                                    indicatorManageEntity.setMissCompleteMethod("前向填充");
                                }else{ // 文本离散
                                    System.out.println("文本离散");
                                    indicatorManageEntity.setMissCompleteMethod("前向填充");
                                    indicatorManageEntity.setFeatureDataType(3);
                                }
                            }
                        }else{ // 非离散
                            System.out.println("bbbb");
                            if(fieldMap.get(tableField).equals("integer") || fieldMap.get(tableField).equals("double precision")) { // 数字类型
                                System.out.println("数字连续");
                                indicatorManageEntity.setFeatureDataType(1);
                                indicatorManageEntity.setMissCompleteMethod("均数替换");
                            }else continue;
                        }
                    }else{ // 数据表中该字段值全部为空  不需要设置默认的填充类型 没法填充
                        indicatorManageEntity.setDiscrete(false); // 默认设置非离散
                        indicatorManageEntity.setMissRate(100.0f); // 缺失率百分之百
                        if (fieldMap.get(tableField).equals("integer") || fieldMap.get(tableField).equals("double precision")){ // 数字类型
                            indicatorManageEntity.setFeatureDataType(1); // 数字类型
                        }else continue; // 文本但又非离散
                    }
                    list.add(indicatorManageEntity);
                }
                System.out.println("返回值："+list);
                return list;
            }else{
                return null;
            }
        }
    }
    @Override
    public List<IndicatorsMissDataVo> getIndicatorsInfo(List<IndicatorManageEntity> checkedFeats, String tableName) {
        // 跟据特征名称和表名查询有效值，缺失值个数
        List<IndicatorsMissDataVo> indicatorsMissDataVos = new ArrayList<>();
        for (IndicatorManageEntity checkedFeat : checkedFeats) {
            IndicatorsMissDataVo vo  = indicatorManagementMapper.getMissDataInfo(checkedFeat,tableName);
            vo.setIndex(checkedFeat.getFeatureName());
            vo.setMissCompleteMethod(checkedFeat.getMissCompleteMethod());
            indicatorsMissDataVos.add(vo);
        }
        return indicatorsMissDataVos;
    }

    @Override
    public List<Map<String,IsFillVo>> fillData(DataFillMethodVo dataFillMethodVo) {
        System.out.println("缺失值补齐任务参数测试："+dataFillMethodVo.getNewTaskInfo());
        List<IndicatorsMissDataVo> missCompleteMethod = dataFillMethodVo.getMissCompleteMethod();
        List<Map<String,IsFillVo>> res = new ArrayList<>();
        List<List<IsFillVo>> tempRes = new ArrayList<>();
        // 调用远程算法进行缺失值补齐  传递表名称 字段名称
        try
        {
            for (IndicatorsMissDataVo vo : missCompleteMethod) {
                System.out.println("算法为："+vo.getMissCompleteMethod());
                MissDataCompleteMethods missDataCompleteMethods = new MissDataCompleteMethods();
                String path = missDataCompleteMethods.methodMap.get(vo.getMissCompleteMethod());
                System.out.println("路径为："+path);
                 if(path==null || path.equals("")) continue;
                List<String> cols = new ArrayList<>();
                cols.add(vo.getIndex());
                RunPyEntity runPyEntity = new RunPyEntity(dataFillMethodVo.getTableName(),path,cols); // 每一列都是不同的填充算法
                JsonNode jsonNode = HTTPUtils.postRequest(runPyEntity, path);
                List<String> oldData = getJsonNodeData(jsonNode,"old_data"); // 不论该列是和类型都将起数据封装成String类型
                List<String> new_data = getJsonNodeData(jsonNode, "new_data");
                // 封装该列数据的每一行值是否是被填充的
                List<IsFillVo> isFillVos = new ArrayList<>();
                for (int i=0; i<new_data.size(); i++) { // 这是某一列的插补处理
                    IsFillVo isFillVo = new IsFillVo();
                    isFillVo.setColName(vo.getIndex()); // 列名
                    isFillVo.setValue(new_data.get(i));
                    if(oldData.get(i)==null|| oldData.get(i).equals("null") || oldData.get(i).equals("NaN"))  isFillVo.setFlag(true);
                    else isFillVo.setFlag(false);
                    isFillVos.add(isFillVo);

                }
                tempRes.add(isFillVos);
            }
            // 将tempRes封装到res
            System.out.println("tempRes = "+JSON.toJSONString(tempRes));
            int colLength = tempRes.size();
            int rowlength = tempRes.get(0).size();
            for(int j=0; j<rowlength; j++){ // 行
                // 封装一行的数据
                LinkedHashMap<String, IsFillVo> map = new LinkedHashMap<>();
                for(int i=0; i<colLength; i++){ // 列
                    map.put(tempRes.get(i).get(j).getColName(),tempRes.get(i).get(j));
                }
                res.add(map);
            }

            List<String> featureNames = dataFillMethodVo.getMissCompleteMethod().stream().map(indicatorsMissDataVo -> {
                return indicatorsMissDataVo.getIndex();
            }).collect(Collectors.toList());
            String str = featureNames.stream().collect(Collectors.joining(","));
            String models = dataFillMethodVo.getMissCompleteMethod().stream().map(indicatorsMissDataVo -> {
                return indicatorsMissDataVo.getMissCompleteMethod();
            }).collect(Collectors.joining(","));


            List<Task> list = taskMapper.selectList(null);
            List<Task> isRepeat = list.stream().filter(task -> {

                return "缺失值补齐".equals(task.getTasktype()) && task.getFeature().equals(str) && task.getDataset().equals(dataFillMethodVo.getTableName()) && task.getTaskname().equals(dataFillMethodVo.getNewTaskInfo().getTaskName());
            }).collect(Collectors.toList());
            if(isRepeat == null || isRepeat.size() == 0) {
                // 创建任务模型
                Task task = new Task();
                // 获取当前时间的 LocalDateTime 对象
                LocalDateTime now = LocalDateTime.now();
                Timestamp timestamp = Timestamp.valueOf(now);
                task.setCreatetime(timestamp);
                task.setDataset(dataFillMethodVo.getTableName());
                task.setFeature(str); //有哪些列
                task.setTargetcolumn(str);

                String leader = dataFillMethodVo.getNewTaskInfo().getPrincipal();
                if(leader == null || leader=="") leader = UserThreadLocal.get().getUsername();
                task.setLeader(leader);
                task.setModel(models); // 每列的插补算法

                String taskName = dataFillMethodVo.getNewTaskInfo().getTaskName();
                if( taskName == null|| taskName=="") taskName = UserThreadLocal.get().getUsername()+"_"+"缺失值补齐_"+ LocalDate.now().toString();
                task.setTaskname(taskName);
                task.setTasktype(dataFillMethodVo.getNewTaskInfo().getTasktype()==null?"缺失值补齐":dataFillMethodVo.getNewTaskInfo().getTasktype());

                task.setParticipant(dataFillMethodVo.getNewTaskInfo().getParticipants());
                task.setRemark(dataFillMethodVo.getNewTaskInfo().getTips());

                // 跟据表名获取父节点的名称 select label from category where "id"=(select parent_id from category where label='copd')
                String label = categoryMapper.getParentLabelByLabel(dataFillMethodVo.getTableName());
                task.setDisease(label);
                task.setUserid(UserThreadLocal.get().getUid());
                taskMapper.insert(task);
            }

        }catch (Exception e){
            e.printStackTrace();
        }



        return res;
    }
    private List<String> getJsonNodeData(JsonNode jsonNode,String key){
        JsonNode old_data = jsonNode.get(key);
        List<String> dataList = new ArrayList<>();
        if (old_data.isArray()) {
            for (JsonNode row : old_data) {
                if (row.isArray()){
                    dataList.add(row.get(0).asText());
                }else{
                    dataList.add(row.asText());
                }
            }
        }
        return dataList;
    }
}
