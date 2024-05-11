package com.cqupt.software_1.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqupt.software_1.common.R;
import com.cqupt.software_1.entity.Notification;
import com.cqupt.software_1.service.NoticeService;
import com.cqupt.software_1.vo.InsertNoticeVo;
import com.cqupt.software_1.vo.InsertUserVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/allNotices")
    public PageInfo<Notification> allNotices(@RequestParam Integer pageNum , @RequestParam Integer pageSize){
        return noticeService.allNotices(pageNum, pageSize);
    }

    @GetMapping("/queryNotices")
    public List<Notification> queryNotices(){
        return noticeService.queryNotices();
    }




    @PostMapping("/updateNotice")
    public R updateNotice(@RequestBody Notification notification){

        notification.setUpdateTime(new Date());
        noticeService.saveOrUpdate(notification);

        return new R<>(200 , "成功", null);
    }


    @PostMapping("delNotice")
    public R delNotice(@RequestBody Notification notification){
        noticeService.removeById(notification.getInfoId());
        return new R<>(200 , "成功", null);
    }

    @PostMapping("insertNotice")
    public R insertNotice(@RequestBody InsertNoticeVo notification){

        noticeService.saveNotification(notification);
        return new R<>(200 , "成功", null);
    }


}
