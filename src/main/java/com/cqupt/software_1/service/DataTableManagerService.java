package com.cqupt.software_1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software_1.common.DataTable;

import java.util.List;
import java.util.Map;

public interface DataTableManagerService extends IService<DataTable> {

    void updateDataTable(String table_name,String disease);

    List<DataTable> upalldata();

    void deletename(String tableName);


    List<String> upname();

    List<Map<String, Object>> getTableData(String tableId, String tableName);
}
