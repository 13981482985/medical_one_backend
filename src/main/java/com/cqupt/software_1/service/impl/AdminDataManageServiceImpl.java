package com.cqupt.software_1.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cqupt.software_1.entity.CategoryEntity;
import com.cqupt.software_1.entity.TableDescribeEntity;
import com.cqupt.software_1.mapper.AdminDataManageMapper;
import com.cqupt.software_1.mapper.CategoryMapper;
import com.cqupt.software_1.mapper.TableDataMapper;
import com.cqupt.software_1.mapper.UserMapper;
import com.cqupt.software_1.service.AdminDataManageService;
import com.cqupt.software_1.service.LogService;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
* @author hp
* @description 针对表【t_table_manager】的数据库操作Service实现
* @createDate 2023-05-23 15:10:20
*/
@Service
public class AdminDataManageServiceImpl extends ServiceImpl<AdminDataManageMapper, TableDescribeEntity>
    implements AdminDataManageService {
    @Autowired
    private AdminDataManageMapper adminDataManageMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private LogService logService;
    @Autowired
    TableDataMapper tableDataMapper;

//    @Transactional(propagation = Propagation.REQUIRED)
//    public List<String> storeTableData(MultipartFile file, String tableName) throws IOException {
//        ArrayList<String> featureList = null;
//        if (!file.isEmpty()) {
//            // 使用 OpenCSV 解析 CSV 文件
//            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(),"UTF-8"));
//            CSVReader csvReader = new CSVReader(reader);
//            List<String[]> csvData = csvReader.readAll();
//            csvReader.close();
//            // 获取表头信息
//            String[] headers = csvData.get(0);
//            featureList = new ArrayList<String>(Arrays.asList(headers));
//            System.out.println("表头信息为："+ JSON.toJSONString(headers));
//            // 删除表头行，剩余的即为数据行
//            csvData.remove(0);
//            // 创建表信息
//            adminDataManageMapper.createTable(headers,tableName);
//            // 保存表头信息和表数据到数据库中
//            for (String[] row : csvData) { // 以此保存每行信息到数据库中
//                adminDataManageMapper.insertRow(row,tableName);
//            }
//        }
//        return featureList;
//    }
@Transactional(propagation = Propagation.REQUIRED)
public List<String> storeTableData(MultipartFile file, String tableName) throws IOException {
    ArrayList<String> featureList = null;
    if (!file.isEmpty()) {
        // 使用 OpenCSV 解析 CSV 文件
        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(),"UTF-8"));
        CSVReader csvReader = new CSVReader(reader);
        List<String[]> csvData = csvReader.readAll();
        csvReader.close();
        // 获取表头信息
        String[] headers = csvData.get(0);
        featureList = new ArrayList<String>(Arrays.asList(headers));
        // 删除表头行，剩余的即为数据行
        csvData.remove(0);
        // 解析字段类型
        String[] featureDataType = getFeatureDataType(csvData);
        // 创建表信息
        tableDataMapper.createTable2(headers,featureDataType, tableName);

//            // 批量
        // TODO 分批插入 防止sql参数传入过多导致溢出
        if(csvData.size()>10000){
            int batch = csvData.size()/10000;
            for(int i=0; i<batch; i++){
                int start = i*10000, end = (i+1)*10000;
                tableDataMapper.batchInsertCsv(csvData.subList(start,end),tableName); // diseaseData.subList(start,end) 前闭后开
            }
            tableDataMapper.batchInsertCsv(csvData.subList(batch*10000,csvData.size()),tableName);
        }else{
            tableDataMapper.batchInsertCsv(csvData,tableName);
        }
        // TODO 改进
//            int count = csvData.size();
//            int pageSize = 5000;
//            int threadNum = count % pageSize == 0 ?  count / pageSize:  count / pageSize + 1;
//            CountDownLatch downLatch = new CountDownLatch(threadNum);
//            long start = System.currentTimeMillis();
//
//            for (int i = 0; i < threadNum; i++) {
//                //开始序号
//                int startIndex = i * pageSize;
//                //结束序号
//                int endIndex = Math.min(count, (i+1)*pageSize);
//                //分割list
//                List<String[]> epochData = csvData.subList(startIndex, endIndex);
//
//                taskExecutor.execute(() -> {
//                    try {
//                        tableDataMapper.batchInsertCsv(epochData,tableName);
//
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }finally {
//                        //执行完后 计数
//                        downLatch.countDown(); // 计数器减1
//                    }
//                });
//            }
//            try {
//                //等待
//                downLatch.await(); //等待所有的线程执行结束 当里面的数值变为0的时候主线程就不在等待
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            long end = System.currentTimeMillis();
//            System.out.println("插入数据耗费时间："+(end-start));

    }
    return featureList;
}
    private String[] getFeatureDataType(List<String[]> csvData) {
        String[] dataType = new String[csvData.get(0).length];
        for (String[] csvDatum : csvData) {  //遍历每一行
            for (int i=0; i<csvDatum.length; i++) { // 遍历每一列
                try {
                    Float.parseFloat(csvDatum[i]); // 可以转成数字
                }catch (Exception e){ // 不可以转成数字
                    dataType[i] = "VARCHAR(255)";
                }
            }
        }
        for(int i=0; i<csvData.get(0).length; i++){
            if(dataType[i]==null) dataType[i]="float8";
        }
        System.out.println("字段类型为："+JSON.toJSONString(dataType));
        return dataType;
    }
    @Override
    @Transactional
    public List<String> uploadDataTable(MultipartFile file, String pid, String tableName, String userName,
                                        String classPath, String uid, String tableStatus, float tableSize,
                                        String current_uid, String uid_list) throws IOException, ParseException {
        // 封住表描述信息
        TableDescribeEntity adminDataManageEntity = new TableDescribeEntity();
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setCatLevel(4);
        categoryEntity.setLabel(tableName);
        categoryEntity.setParentId(pid);
        categoryEntity.setIsLeafs(1);
        categoryEntity.setIsDelete(0);
        categoryEntity.setUid(uid);
        categoryEntity.setStatus(tableStatus);
        categoryEntity.setUidList(uid_list);
        categoryEntity.setUsername(userName);

        // 杨星 修改  字段类型
        categoryEntity.setIs_upload("1");
        categoryEntity.setIs_filter("0");
        System.out.println("==categoryEntity==" + categoryEntity );
        categoryMapper.insert(categoryEntity);
        logService.insertLog(current_uid, 0, "在category中增加了"+tableName);


        adminDataManageEntity.setTableName(tableName);
        adminDataManageEntity.setTableId(categoryEntity.getId());
        adminDataManageEntity.setCreateUser(userName);
        // 解析系统当前时间
        adminDataManageEntity.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        adminDataManageEntity.setClassPath(classPath);
        adminDataManageEntity.setUid(Integer.parseInt(uid));
        adminDataManageEntity.setTableStatus(tableStatus);
        adminDataManageEntity.setTableSize(tableSize);
        adminDataManageMapper.insert(adminDataManageEntity);
        logService.insertLog(current_uid, 0, "在table_describe中上传了"+tableName);

        userMapper.minusTableSize(Integer.parseInt(uid), tableSize);
        logService.insertLog(current_uid, 0, "在user表中修改容量" );
        List<String> featureList = storeTableData(file, tableName);
        logService.insertLog(current_uid, 0, "在public模式下中创建了"+tableName);
        // 保存数据库
        System.out.println("表描述信息插入成功, 动态建表成功");
        return featureList;
    }

    @Override
    public List<TableDescribeEntity> selectAllDataInfo() {
        return adminDataManageMapper.selectAllDataInfo();
    }

    @Override
    public List<TableDescribeEntity> selectDataByName(String searchType, String name) {
        return adminDataManageMapper.selectDataByName(searchType, name);
    }

    @Override
    public TableDescribeEntity selectDataById(String id) {
        return adminDataManageMapper.selectDataById(id);
    }

    @Override
    public void deleteByTableName(String tableName) {
        adminDataManageMapper.deleteByTableName(tableName);
    }
    @Override
    public void deleteByTableId(String tableId) {
        adminDataManageMapper.deleteByTableId(tableId);
    }

    @Override
    public void updateById(String id, String tableName, String tableStatus) {
        TableDescribeEntity adminDataManage = adminDataManageMapper.selectById(id);
        String classPath = adminDataManage.getClassPath();
        String[] str = classPath.split("/");
        str[str.length-1] = tableName;
        classPath = String.join("/", str);
        adminDataManage.setClassPath(classPath);
        adminDataManage.setTableName(tableName);
        adminDataManage.setTableStatus(tableStatus);
        adminDataManageMapper.updateById(adminDataManage);
//        adminDataManageMapper.updateById(id, tableName, tableStatus);
    }

    @Override
    public void updateDataBaseTableName(String old_name, String new_name){
        adminDataManageMapper.updateDataBaseTableName(old_name, new_name);
    }

    @Override
    @Transactional
    public void updateInfo(String id, String tableid, String oldTableName, String tableName, String tableStatus, String current_uid) {
        updateById(id, tableName, tableStatus);
        logService.insertLog(current_uid, 0, "更改了table_describe表中的"+oldTableName + "表为：" + tableName + ",将状态更改为：" + tableStatus + "并更改了classpath");
        categoryMapper.updateTableNameByTableId(tableid, tableName, tableStatus);
        logService.insertLog(current_uid, 0, "更改了category表中的"+oldTableName + "表为：" + tableName + ",将状态更改为：" + tableStatus);
        if (!oldTableName.equals(tableName)){
            updateDataBaseTableName(oldTableName, tableName);
            logService.insertLog(current_uid, 0, "更改了数据库中的"+oldTableName + "表为：" + tableName );
        }
    }


}




