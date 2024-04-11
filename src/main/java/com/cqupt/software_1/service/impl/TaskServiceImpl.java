package com.cqupt.software_1.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cqupt.software_1.common.UserThreadLocal;
import com.cqupt.software_1.entity.AlgorithmUsageDailyStats;
import com.cqupt.software_1.entity.Task;
import com.cqupt.software_1.mapper.CategoryMapper;
import com.cqupt.software_1.mapper.TaskMapper;
import com.cqupt.software_1.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
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
    public void createVisualizationTask(String tableName, Map<String, Object> selectDiseaseMap) {
        Task task = new Task();
        String features = selectDiseaseMap.keySet().stream().collect(Collectors.joining(","));
        task.setFeature(features);
        task.setTargetcolumn(features);
        task.setCreatetime(new Timestamp(System.currentTimeMillis()));
        task.setDataset(tableName);
        task.setLeader(UserThreadLocal.get().getUsername());
        task.setTaskname("病人画像");
        String label = categoryMapper.getParentLabelByLabel(tableName);
        task.setDisease(label);
        task.setRemark("病人画像分析");
        task.setModel("无");
        task.setResult(JSON.toJSONString(selectDiseaseMap));
        task.setUserid(UserThreadLocal.get().getUid());
        taskMapper.insert(task);
    }

    @Override
    public Integer getTaskCountByTime(String timeStr) {
        return taskMapper.getTaskCountByTime(timeStr);
    }

}
