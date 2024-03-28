package com.cqupt.software_1.service;

import com.cqupt.software_1.entity.CategoryEntity;
import com.cqupt.software_1.vo.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;

// TODO 公共模块新增类
public interface TableDataService {
    List<LinkedHashMap<String,Object>> getTableData(String TableId, String tableName);

    List<String> uploadFile(MultipartFile file, String tableName, String type, String user, int userId, String parentId, String parentType) throws IOException, ParseException;

    void createTable(String tableName, List<CreateTableFeatureVo> characterList, String createUser, CategoryEntity nodeData);

    List<LinkedHashMap<String, Object>> getFilterDataByConditions(List<CreateTableFeatureVo> characterList,CategoryEntity nodeData);

    void exportFile(ExportFilledDataTableVo dataFillMethodVo);

    FeatureDescAnaVo featureDescAnalyze(String featureName, String tableName) throws IOException, URISyntaxException;

    SingleAnalyzeVo singleFactorAnalyze(String tableName, List<String> colNames) throws IOException, URISyntaxException;
}
