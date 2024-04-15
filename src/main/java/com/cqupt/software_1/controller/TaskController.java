package com.cqupt.software_1.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.software_1.common.R;
import com.cqupt.software_1.common.TaskRequest;
import com.cqupt.software_1.entity.CreateTaskEntity;
import com.cqupt.software_1.entity.Task;
import com.cqupt.software_1.service.TaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sun.prism.shader.AlphaOne_Color_AlphaTest_Loader;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/Task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping("/getOne/{id}")
    public R getOne(@PathVariable int id){
        Task task = taskService.getById(id);

        TaskRequest taskRequest = new TaskRequest();
        BeanUtils.copyProperties(task,taskRequest);
        if (!StringUtils.isEmpty(task.getParameters())){
            taskRequest.setPara(task.getParameters().split(","));
        }else {
            taskRequest.setPara(new String[]{});
        }


        return R.success(task);
    }

    @PostMapping("/addTask")
    public R addTask(@RequestBody Task task){
        taskService.save(task);
        return R.success(200,"创建成功");
    }

    @GetMapping("/getLeaderList")
    public R getLeaderList(){
        List<String> leaderList = taskService.getLeaderList();
        return R.success(leaderList);
    }





    @GetMapping("/all")
    public R getTaskList() {
        return R.success(taskService.getTaskList());
    }

    @GetMapping("/result/{id}")
    public R result(@PathVariable int id) throws JsonProcessingException {
        Task task = taskService.getlistbyId(id);
        TaskRequest request = new TaskRequest();
        System.out.println(task);
        ObjectMapper objectMapper = new ObjectMapper();
        String res = task.getResult();
        String[][] retrievedArray = objectMapper.readValue(res, String[][].class);

        String fea1 = task.getFeature();
        String[] fea = fea1.split(",");
        String tar1 = task.getTargetcolumn();
        String[] tar = tar1.split(",");
        String para1 = task.getParameters();
        String[] para = new String[0];

        if (para1 != null)
        {
            para = para1.split(",");
        }
        String paraV1 = task.getParametersvalues();
        String[] paraV = new String[0];
        if (paraV1!=null){
            paraV = paraV1.split(",");
        }
        request.setCi(task.getCi());
        request.setRatio(String.valueOf(task.getRatio()));
        request.setRes(retrievedArray);
        request.setTime(task.getUsetime());
        request.setDisease(task.getDisease());
        request.setDisease(task.getDisease());
        request.setFeature(fea);
        request.setLeader(task.getLeader());
        request.setModel(task.getModel());
        request.setPara(para);
        request.setParaValue(paraV);
        request.setParticipant(task.getParticipant());
        request.setTargetcolumn(tar);
        request.setTaskName(task.getTaskname());
        request.setDataset(task.getDataset());
        request.setUid(task.getUserid());
        return R.success(request);
    }
    @GetMapping("/delete/{id}")
    public R deleteById(@PathVariable int id){
        taskService.deleteTask(id);
        return R.success(taskService.getTaskList());
    }

    @GetMapping("/visualization")
    public R visualizationTask(@RequestParam("tableName") String tableName, @RequestParam("selectDisease") String diseaseData,@RequestParam(value = "taskInfo",required = false) String taskInfo) throws JsonProcessingException {
        // 将 JSON 字符串转换为map对象
        ObjectMapper objectMapper = new ObjectMapper();
        CreateTaskEntity createTaskEntity = new Gson().fromJson(taskInfo, CreateTaskEntity.class);
        Map<String, Object> selectDiseaseMap = objectMapper.readValue(diseaseData, Map.class);
        taskService.createVisualizationTask(tableName,selectDiseaseMap,createTaskEntity);
        return R.success(200,"病人画像任务创建成功");
    }

    @GetMapping("/addRepresentTask")
    public R addRepresentTask(@RequestParam("tableName") String tableName, @RequestParam("colNames")List<String> colNames,@RequestParam("model") String model, @RequestParam("taskInfo")String taskInfo){
        CreateTaskEntity createTaskEntity = new Gson().fromJson(taskInfo, CreateTaskEntity.class);
        taskService.addRepresentTask(tableName,colNames,model,createTaskEntity);
        return R.success(200,"特征表征任务创建成功");
    }

    @GetMapping("/filterTaskBypage")
    public R filterTaskList(@RequestParam(value = "disease",required = false) String disease,
                            @RequestParam(value = "tasktype",required = false)String tasktype,
                            @RequestParam(value = "leader",required = false)String leader,
                            @RequestParam(value = "newPage",required = false)Integer newPage,
                            @RequestParam(value = "pageSize",required = false) Integer pageSize){
        System.out.println("参数为："+disease + tasktype + leader+newPage+pageSize);
        List<Task> tasks = filterTask(disease, tasktype, leader);
        int start = (newPage-1)*pageSize;
        int end = newPage*pageSize;
        if(tasks.size()<end) return R.success(tasks.subList(start,tasks.size()));
        else return R.success(tasks.subList(start,end));
    }

    public List<Task> filterTask(String disease,String tasktype,String leader){
        if(disease!=null && tasktype!=null && leader!=null){
            return taskService.list(new QueryWrapper<Task>().eq("disease",disease).eq("leader",leader).eq("tasktype",tasktype));
        }else if(disease==null && tasktype!=null && leader!=null){
            return taskService.list(new QueryWrapper<Task>().eq("leader",leader).eq("tasktype",tasktype));
        }else if(disease!=null && tasktype==null && leader!=null){
            return taskService.list(new QueryWrapper<Task>().eq("leader",leader).eq("disease",disease));
        }else if(disease!=null && tasktype!=null && leader==null){
            return taskService.list(new QueryWrapper<Task>().eq("tasktype",tasktype).eq("disease",disease));
        }else if(disease==null && tasktype!=null && leader==null){
            return taskService.list(new QueryWrapper<Task>().eq("tasktype",tasktype));
        }else if(disease==null && tasktype==null && leader!=null){
            return taskService.list(new QueryWrapper<Task>().eq("leader",leader));
        }else if(disease!=null && tasktype==null && leader==null){
            return taskService.list(new QueryWrapper<Task>().eq("disease",disease));
        }else{
            return taskService.list(null);
        }
    }

    @GetMapping("/staticTaskTrend")
    public R staticTaskTrend(){
        // 获取当期那时间前7天的数据量
        LocalDate now = LocalDate.now();
        Map<String,Integer> res = new HashMap<>();
        for(int i=7; i>=0; i--){
            LocalDate time = now.minusDays(i);
            String timeStr = time.toString();
            res.put(timeStr,taskService.getTaskCountByTime(timeStr));
        }
        return R.success("200",res);
    }

    @GetMapping("getTaskNumber")
    public R getTaskNumber(String disease,String tasktype,String leader){
        int size = filterTask(disease,tasktype,leader).size();
        return R.success("200",size);
    }


}
