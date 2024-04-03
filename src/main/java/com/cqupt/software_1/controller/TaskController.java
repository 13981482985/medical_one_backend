package com.cqupt.software_1.controller;

import com.cqupt.software_1.common.R;
import com.cqupt.software_1.common.TaskRequest;
import com.cqupt.software_1.entity.Task;
import com.cqupt.software_1.service.TaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;
import java.util.List;


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

}
