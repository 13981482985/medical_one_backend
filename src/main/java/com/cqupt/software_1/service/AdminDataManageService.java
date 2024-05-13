package com.cqupt.software_1.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.cqupt.software_1.entity.TableDescribeEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

// TODO 公共模块新增类
public interface AdminDataManageService extends IService<TableDescribeEntity> {
    List<String>  uploadDataTable(MultipartFile file, String pid, String tableName, String userName, String classPath, String uid, String tableStatus, float tableSize, String current_uid, String uid_list) throws IOException, ParseException;

    List<TableDescribeEntity> selectAllDataInfo();

    List<TableDescribeEntity> selectDataByName(String searchType, String name);
    TableDescribeEntity selectDataById(String id);

    void deleteByTableName(String tablename);
    void deleteByTableId(String tableId);

    void updateById(String id,String[] pids, String tableName, String tableStatus);
    void updateDataBaseTableName(String old_name, String new_name);

    void updateInfo(String id, String tableid, String oldTableName, String tableName, String tableStatus, String[] pids,String current_uid);
}
