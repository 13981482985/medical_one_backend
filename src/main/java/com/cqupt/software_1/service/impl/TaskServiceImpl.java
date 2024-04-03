package com.cqupt.software_1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cqupt.software_1.entity.AlgorithmUsageDailyStats;
import com.cqupt.software_1.entity.Task;
import com.cqupt.software_1.mapper.TaskMapper;
import com.cqupt.software_1.service.TaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
        implements TaskService {
    @Resource
    TaskMapper taskMapper;

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

}
