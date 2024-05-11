package com.cqupt.software_1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software_1.entity.Notification;
import com.cqupt.software_1.entity.User;
import com.cqupt.software_1.mapper.NoticeMapper;
import com.cqupt.software_1.mapper.UserMapper;
import com.cqupt.software_1.service.NoticeService;
import com.cqupt.software_1.service.UserService;
import com.cqupt.software_1.vo.InsertNoticeVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notification>
        implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;


    @Override
    public PageInfo<Notification> allNotices(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        // 返回查询结果列表
        List<Notification> notifications =  noticeMapper.selectAllNotices();
        // 使用 PageInfo 包装查询结果，并返回
        return new PageInfo<>(notifications);
    }

    @Override
    public void saveNotification(InsertNoticeVo notification) {
        noticeMapper.saveNotification(notification);
    }

    @Override
    public List<Notification> queryNotices() {


        // 创建QueryWrapper对象
        QueryWrapper queryWrapper = new QueryWrapper<>();

        // 设置按照时间字段createTime进行降序排序
        queryWrapper.orderByDesc("update_time");

        List<Notification> notifications = noticeMapper.selectList(queryWrapper);
        return notifications;
    }
}
