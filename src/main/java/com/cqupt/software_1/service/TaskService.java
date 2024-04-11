
package com.cqupt.software_1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software_1.entity.AlgorithmUsageDailyStats;
import com.cqupt.software_1.entity.Task;

import java.util.List;
import java.util.Map;

public interface TaskService extends IService<Task> {
    List<AlgorithmUsageDailyStats> getAlgorithmUsageDailyStatsLast7Days();

    List<String> getAlgorithmName();

    List<Task> getTaskList();

    Task getlistbyId(Integer id);

    void deleteTask(int id);
    List<String> getLeaderList();

    void createVisualizationTask(String tableName, Map<String, Object> selectDiseaseMap);

    Integer getTaskCountByTime(String timeStr);
}
