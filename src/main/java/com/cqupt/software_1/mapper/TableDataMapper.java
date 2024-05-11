package com.cqupt.software_1.mapper;

import com.cqupt.software_1.vo.CreateTableFeatureVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.*;

// TODO 公共模块新增类

@Mapper
public interface TableDataMapper {
    List<LinkedHashMap<String,Object>> getTableData(String tableName);

    void createTable(@Param("headers") String[] headers,@Param("tableName") String tableName);

    void insertRow(@Param("row") String[] row, @Param("tableName") String tableName);

    List<LinkedHashMap<String, Object>> getFilterData(@Param("tableName") String label, @Param("conditions") List<CreateTableFeatureVo> characterList);

    void createTableByField(@Param("tableName") String tableName, @Param("fieldMap") HashMap<String, String> fieldMap);

    void bachInsertData(@Param("diseaseData") List<LinkedHashMap<String, Object>> diseaseData,@Param("tableName") String tableName);

    List<LinkedHashMap<String, Object>> getColsTableData(@Param("colsName") ArrayList<String> colNames, @Param("tableName") String tableName);

    List<LinkedHashMap<String, Object>> getAllTableData(@Param("tableName") String label);

    Integer getCountByCondition(@Param("start") String valueOf, @Param("end") String valueOf1,@Param("tableName") String tableName,@Param("featureName") String featureName);

    Integer getAllCount(@Param("tableName") String tableName);

    Integer getCount(@Param("featureValue") String featureValue, @Param("tableName") String tableName, @Param("featureName") String featureName);

    List<Map<String, Object>> getTableDataByFields(@Param("tableName")String tableName, @Param("featureList") List<String> featureList);

    int getCountByName(String tableName);

    void batchInsertCsv(@Param("csvData") List<String[]> csvData, @Param("tableName") String tableName);


    List<Map<String, Object>> getInfoByTableName(String tableName);

    Integer getCountByTableName(String tableName);


    void createTable2(@Param("fields") String[] headers, @Param("dataType") String[] featureDataType, @Param("tableName") String tableName);

    Object[] getDistinctValue(@Param("tableName") String tableName, @Param("featureName") String featureName);

    List<String> getTableFields(String tableName);

    List<LinkedHashMap<String, Object>> getDataByLikeMatch(@Param("tableName") String tableName, @Param("tableFields") List<String> tableFields, @Param("value") String value);

}
