package com.cqupt.software_1.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cqupt.software_1.common.UserThreadLocal;
import com.cqupt.software_1.entity.AlgorithmUsageDailyStats;
import com.cqupt.software_1.entity.CreateTaskEntity;
import com.cqupt.software_1.entity.Task;
import com.cqupt.software_1.mapper.CategoryMapper;
import com.cqupt.software_1.mapper.TaskMapper;
import com.cqupt.software_1.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
        implements TaskService {
    @Resource
    TaskMapper taskMapper;
    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public List<AlgorithmUsageDailyStats> getAlgorithmUsageDailyStatsLast7Days() {
        return taskMapper.getAlgorithmUsageDailyStatsLast7Days();
    }

    @Override
    public List<String> getAlgorithmName() {
        return taskMapper.getAlgorithmName();
    }

    @Override
    public List<Task> getTaskList() {
        return taskMapper.getTaskList();
    }

    @Override
    public Task getlistbyId(Integer id) {
        return taskMapper.getlistbyId(id);
    }

    @Override
    public void deleteTask(int id) {
        taskMapper.deleteTask(id);
    }

    @Override
    public List<String> getLeaderList() {
        return taskMapper.getLeaderList();
    }

    @Override
    public void createVisualizationTask(String tableName, Map<String, Object> selectDiseaseMap, CreateTaskEntity taskInfo) {
        Task task = new Task();
        String features = selectDiseaseMap.keySet().stream().collect(Collectors.joining(","));
        task.setFeature(features);
        task.setTargetcolumn(features);
        task.setCreatetime(new Timestamp(System.currentTimeMillis()));
        task.setDataset(tableName);

        task.setLeader(taskInfo.getPrincipal()==null?UserThreadLocal.get().getUsername():taskInfo.getPrincipal());
        task.setParticipant(taskInfo.getParticipants());
        task.setTasktype(taskInfo.getTasktype()==null?"病人画像":taskInfo.getTasktype());
        task.setTaskname(taskInfo.getTaskName()==null?UserThreadLocal.get().getUsername()+"_病人画像_"+ LocalDate.now().toString():taskInfo.getTaskName());

        String label = categoryMapper.getParentLabelByLabel(tableName);
        task.setDisease(label);
        task.setRemark(taskInfo.getTips());
        task.setModel("无");
        task.setResult(JSON.toJSONString(selectDiseaseMap));
        task.setUserid(UserThreadLocal.get().getUid());
        taskMapper.insert(task);
    }

    @Override
    public Integer getTaskCountByTime(String timeStr) {
        return taskMapper.getTaskCountByTime(timeStr);
    }

    @Override
    public void addRepresentTask(String tableName, List<String> colNames,String model, CreateTaskEntity taskInfo) {
        Task task = new Task();
        String features = colNames.stream().collect(Collectors.joining(","));
        task.setFeature(features);
        task.setTargetcolumn(features);

        task.setCreatetime(new Timestamp(System.currentTimeMillis()));
        task.setDataset(tableName);

        task.setLeader(taskInfo.getPrincipal()==null?UserThreadLocal.get().getUsername():taskInfo.getPrincipal());
        task.setParticipant(taskInfo.getParticipants());
        task.setTasktype(taskInfo.getTasktype()==null?"疾病特征表征":taskInfo.getTasktype());
        task.setTaskname(taskInfo.getTaskName()==null?UserThreadLocal.get().getUsername()+"_疾病特征表征_"+ LocalDate.now().toString():taskInfo.getTaskName());
        task.setModel(model);

        String label = categoryMapper.getParentLabelByLabel(tableName);
        task.setDisease(label);
        task.setRemark(taskInfo.getTips());
        task.setModel("无");
        task.setUserid(UserThreadLocal.get().getUid());
        taskMapper.insert(task);
    }

}
