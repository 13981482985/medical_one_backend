package com.cqupt.software_1.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqupt.software_1.common.R;
import com.cqupt.software_1.common.Result;
import com.cqupt.software_1.common.UserThreadLocal;
import com.cqupt.software_1.entity.UserLog;
import com.cqupt.software_1.service.UserLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/userlog")
public class UserLogController {



   @Autowired
   private UserLogService userLogService;


    @GetMapping("/allLog")
    public R<List<UserLog>> queryAllLog(HttpServletRequest request , HttpServletResponse response){

        Integer userId = UserThreadLocal.get().getUid();

        QueryWrapper queryWrapper = new QueryWrapper();

        queryWrapper.eq("uid",userId);

        List list = userLogService.list(queryWrapper);

        return new R<>(200,"成功",list);

    }


    @GetMapping("/getLogByPage")
    public Result queryLogByPage(@RequestParam Integer pageNum,
                                 @RequestParam Integer pageSize,
                                 @RequestParam String username
    ){
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<UserLog> queryWrapper = new QueryWrapper<UserLog>().orderByDesc("op_time");
        queryWrapper.eq(StringUtils.isNotBlank(username),"username",username);
        List<UserLog> list = userLogService.list(queryWrapper);
        PageInfo<UserLog> userLogPageInfo = new PageInfo<>(list);
        return Result.success(userLogPageInfo);
    }

}
