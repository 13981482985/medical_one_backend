package com.cqupt.software_1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software_1.common.Result;
import com.cqupt.software_1.entity.CategoryEntity;
import com.cqupt.software_1.vo.AddDiseaseVo;
import com.cqupt.software_1.vo.UpdateDiseaseVo;
import org.apache.ibatis.annotations.Param;


import java.util.List;

// TODO 公共模块新增类
public interface CategoryService extends IService<CategoryEntity> {
    List<CategoryEntity> getCategory();
    List<CategoryEntity> getCategory(Integer uid);
    void removeNode(String id);

    void addParentDisease(String diseaseName);

    void changeStatus(CategoryEntity categoryEntity);
    /**
     *  合并
     */


    //    新增疾病管理模块
    List<CategoryEntity> getAllDisease();
    int addCategory(AddDiseaseVo addDiseaseVo);
    Result updateCategory(UpdateDiseaseVo updateDiseaseVo);
    void removeCategorys(List<String> deleteIds);



    //    下面方法是管理员端-数据管理新增
//    查看各等级病种
    List<CategoryEntity> getLevel2Label();
    List<CategoryEntity> getLabelsByPid(@Param("pid") String pid);

}
