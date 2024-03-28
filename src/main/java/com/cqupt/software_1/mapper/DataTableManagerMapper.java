package com.cqupt.software_1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.software_1.common.DataTable;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataTableManagerMapper extends BaseMapper<DataTable> {

    void insertDataTable(DataTable dataTable);

    List<DataTable> upalldata();

    void deletetablename(String tablename);

    List<String> upname();

    List<Map<String, Object>> getTableData(String tableName);
}
