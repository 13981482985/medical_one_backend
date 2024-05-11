package com.cqupt.software_1.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cqupt.software_1.common.R;
import com.cqupt.software_1.common.Result;
import com.cqupt.software_1.entity.Category2Entity;
import com.cqupt.software_1.entity.CategoryEntity;
import com.cqupt.software_1.entity.User;
import com.cqupt.software_1.entity.UserLog;
import com.cqupt.software_1.mapper.CategoryMapper;
import com.cqupt.software_1.mapper.UserMapper;
import com.cqupt.software_1.service.Category2Service;
import com.cqupt.software_1.service.CategoryService;
import com.cqupt.software_1.service.UserLogService;
import com.cqupt.software_1.vo.AddDiseaseVo;
import com.cqupt.software_1.vo.DeleteDiseaseVo;
import com.cqupt.software_1.vo.UpdateDiseaseVo;
import org.hibernate.event.spi.PreCollectionUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO 公共模块新增类
@RestController
@RequestMapping("/api")
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    Category2Service category2Service;
    @Autowired
    UserLogService userLogService;

    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    UserMapper userMapper;
    // 获取目录
//    @GetMapping("/category")
//    public R<List<CategoryEntity>> getCatgory(){
//        List<CategoryEntity> list = categoryService.getCategory();
//        System.out.println(JSON.toJSONString(list));
//        return R.success("200",list);
//    }

    // TODO 新增可共享用户列表
    @PostMapping("/category/changeToShare")
    public Result changeToShare(@RequestBody Map<String, Object> requestData){
        String nodeid = (String) requestData.get("nodeid");
        String uid_list = (String) requestData.get("uid_list");
        CategoryEntity entity = new CategoryEntity();
        entity.setUidList(uid_list);
        entity.setStatus("1");
        UpdateWrapper<CategoryEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", nodeid);
        int res = categoryMapper.update(entity, updateWrapper);
        if(res == 1){
            return Result.success(200,"修改成功");
        }
        else {
            return Result.fail(500,"修改失败");
        }
    }

    //TODO 新增可共享用户列表
    @PostMapping("/category/changeToPrivate")
    public Result changeToPrivate(@RequestBody Map<String, Object> requestData){
        String nodeid = (String) requestData.get("nodeid");
        CategoryEntity entity = new CategoryEntity();
        entity.setUidList("");
        entity.setStatus("0");
        UpdateWrapper<CategoryEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", nodeid);
        int res = categoryMapper.update(entity, updateWrapper);
        if(res == 1){
            return Result.success(200,"修改成功");
        }
        else {
            return Result.fail(500,"修改失败");
        }
    }

    //TODO 新增可共享用户列表
    @PostMapping("/category/getNodeInfo")
    public Result getNodeInfo(@RequestBody Map<String, Object> requestData){
        String nodeid = (String) requestData.get("nodeid");
        String uid = (String) requestData.get("uid");

        QueryWrapper<CategoryEntity> queryWrapper = Wrappers.query();
        queryWrapper.eq("id",nodeid);
        CategoryEntity categoryEntity = categoryMapper.selectOne(queryWrapper);
        String includedUids = categoryEntity.getUidList();
        System.out.println(includedUids);
        //使用 split() 方法返回的数组是一个固定长度的数组，无法修改其大小。
        //可以使用 Arrays.asList() 方法将数组转换为 ArrayList，然后再添加额外的元素。
        List<String> includedUidList = new ArrayList<>(Arrays.asList(includedUids.split(",")));
        System.out.println("---------------------------"+includedUidList);
        includedUidList.add(uid);
        List<Integer> uidList = includedUidList.stream().map(userId->{
            return Integer.parseInt(userId);
        }).collect(Collectors.toList());

        QueryWrapper<User> userQueryWrapper1 = new QueryWrapper<>();
        userQueryWrapper1.notIn("uid", uidList);
        List<User> excludeUserList = userMapper.selectList(userQueryWrapper1);

        QueryWrapper<User> userQueryWrapper2 = new QueryWrapper<>();
        uidList.remove(uid);
        userQueryWrapper2.in("uid", uidList);
        List<User> includeUserList = userMapper.selectList(userQueryWrapper2);


        //
        List<String> tempRes = new ArrayList<>();
        List<Map<String, Object>> included = new ArrayList<>();
        for (User user : includeUserList) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("key", user.getUid());
            resultMap.put("label", user.getUsername());
            tempRes.add(user.getUid().toString());
            included.add(resultMap);
        }


        List<Map<String, Object>> excluded = new ArrayList<>();
        for (User user : excludeUserList) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("key", user.getUid());
            resultMap.put("label", user.getUsername());
            excluded.add(resultMap);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("included", included);
        result.put("excluded", excluded);
        return  Result.success(200,"操作成功", tempRes);
    }











    @GetMapping("/category")
    public R<List<CategoryEntity>> getCatgory(@RequestParam String uid){
        List<CategoryEntity> list = categoryService.getCategory(Integer.parseInt(uid));
        return R.success("200",list);
    }


    // 字段管理获取字段
    @GetMapping("/category2")
    public R <List<Category2Entity>> getCatgory2(){
        List<Category2Entity> list = category2Service.getCategory2();
        return R.success("200",list);
    }
    // 创建一种新的疾病
    @PostMapping("/addDisease")
    public R addDisease(@RequestBody CategoryEntity categoryNode){
        // 首先获取所有的已有疾病列表 判断是否重复
        List<CategoryEntity> list = categoryService.list(null);
        List<CategoryEntity> isRepeat = list.stream().filter(categoryEntity -> {
            return categoryEntity.getLabel().equals(categoryNode.getLabel()) && categoryEntity.getIsDelete()==0;
        }).collect(Collectors.toList());
        if(isRepeat!=null && isRepeat.size()>0){
            return R.fail(300,"该疾病已存在！");
        }
        categoryService.save(categoryNode);
        return R.success(200,"新增目录成功");
    }

    // 删除一个目录
    @GetMapping("/category/remove")
    public R removeCate(CategoryEntity categoryEntity){
        categoryService.removeNode(categoryEntity.getId());
        return R.success(200,"删除成功");
    }

    @GetMapping("/addParentDisease")
    public R addParentDisease(@RequestParam("diseaseName") String diseaseName){
        // 首先获取所有的已有疾病列表 判断是否重复
        List<CategoryEntity> list = categoryService.list(null);
        List<CategoryEntity> isRepeat = list.stream().filter(categoryEntity -> {
            return categoryEntity.getLabel().equals(diseaseName) && categoryEntity.getIsDelete()==0;
        }).collect(Collectors.toList());
        if(isRepeat!=null && isRepeat.size()>0){
            return R.fail(300,"该疾病已存在！");
        }
        categoryService.addParentDisease(diseaseName);
        return R.success(200,"新增疾病成功");
    }

    @GetMapping("/disease/all")
    public R getAllDisease1(){
        List<CategoryEntity> category = categoryService.getCategory();
        List<CategoryEntity> notLeafCat = getNotLeafCat(category);
        // 同时去掉公共数据集节点
        Stream<CategoryEntity> allDiseaseCat = notLeafCat.stream().filter(categoryEntity -> {
            return !categoryEntity.getLabel().equals("公共数据集");
        });
        return R.success("200",allDiseaseCat);
    }
    private List<CategoryEntity> getNotLeafCat(List<CategoryEntity> category){
        // 删除每个叶子节点
        List<CategoryEntity> level1 = category.stream().filter(categoryEntity -> {
            return categoryEntity.getIsLeafs()!=1 && categoryEntity.getStatus()==null; //  返回一级目录下的所有非叶子节点
        }).collect(Collectors.toList());

        for (CategoryEntity categoryEntity : level1) {
            List<CategoryEntity> children = categoryEntity.getChildren();
            if(children!=null && children.size()>0){
                categoryEntity.setChildren(getNotLeafCat(children));
            }
        }
        return level1;
    }

    @GetMapping("/getTableNumber")
    public R getTableNumber(){
        List<CategoryEntity> list = categoryService.list();
        List<CategoryEntity> collect = list.stream().filter(categoryEntity -> {
            return categoryEntity.getIsLeafs()==1;
        }).collect(Collectors.toList());
        return R.success("200",collect.size());
    }


    /**
     *
     *
     *     合并
     */

    //    zongqing新增疾病管理模块
    @GetMapping("/category/getAllDisease")
    public Result<List<CategoryEntity>> getAllDisease(){
        List<CategoryEntity> list = categoryService.getAllDisease();
        return Result.success("200",list);
    }

    /**
     * 新增检查疾病名是否存在
     */
    @GetMapping("/category/checkDiseaseName/{diseaseName}")
    public Result checkDiseaseName(@PathVariable String diseaseName){
        QueryWrapper<CategoryEntity> queryWrapper = Wrappers.query();
        queryWrapper.eq("label", diseaseName)
                .eq("is_delete", 0)
                .isNull("status");
        CategoryEntity category = categoryMapper.selectOne(queryWrapper);
        return category==null?Result.success("200","病种名可用"):Result.fail("400","病种名已存在");
    }


    /**
     * 修改
     * @param
     * @return
     */
    @PostMapping("/category/addCategory")
    public Result addCategory(@RequestBody AddDiseaseVo addDiseaseVo){

        // 转换为字符串的  {"firstDisease":"aaa","icdCode":"121","parentId":"1","uid":"8","username":"yfs"}  新增目录信息：{"firstDisease":"bbb","icdCode":"3434","parentId":"1783122562243362817","uid":"8","username":"yfs"}
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String format = sdf.format(date);
        if(categoryService.addCategory(addDiseaseVo)>0){
            UserLog userLog = new UserLog(null,Integer.parseInt(addDiseaseVo.getUid()),format,"添加病种"+addDiseaseVo.getFirstDisease(),addDiseaseVo.getUsername());
            userLogService.save(userLog);
            return Result.success("添加病种成功");
        }else{
            UserLog userLog = new UserLog(null,Integer.parseInt(addDiseaseVo.getUid()),format,"添加病种失败",addDiseaseVo.getUsername());
            userLogService.save(userLog);
            return Result.fail("添加病种失败");
        }
    }
    @PostMapping("/category/updateCategory")
    public Result updateCategory(@RequestBody UpdateDiseaseVo updateDiseaseVo){
        // 转换为字符串的
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String format = sdf.format(date);

        UserLog userLog = new UserLog(null,Integer.parseInt(updateDiseaseVo.getUid()),format,"修改病种"+updateDiseaseVo.getOldName()+"为"+updateDiseaseVo.getDiseaseName(),updateDiseaseVo.getUsername());
        userLogService.save(userLog);
        return categoryService.updateCategory(updateDiseaseVo);
    }
    @PostMapping("/category/deleteCategory")
    public Result deleteCategory(@RequestBody DeleteDiseaseVo deleteDiseaseVo){

        // 转换为字符串的
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String format = sdf.format(date);
        StringJoiner joiner = new StringJoiner(",");
        for (String str : deleteDiseaseVo.getDeleteNames()) {
            joiner.add(str);
        }
        UserLog userLog = new UserLog(null,Integer.parseInt(deleteDiseaseVo.getUid()),format,"删除病种："+joiner.toString(),deleteDiseaseVo.getUsername());
        userLogService.save(userLog);
        categoryService.removeCategorys(deleteDiseaseVo.getDeleteIds());
        return Result.success("删除成功");
    }



    @GetMapping("/changeStatus")
    public Result changeStatus(CategoryEntity categoryEntity){
        categoryService.changeStatus(categoryEntity);
        return Result.success(200,"修改成功",null);
    }


    @GetMapping("/inspectionOfIsNotCommon")
    public Result inspectionOfIsNotCommon(@RequestParam("newname") String name){
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", 0);
        wrapper.eq("is_common", 0);
        List<CategoryEntity> list = categoryService.list(wrapper);
        List<String>  nameList = new ArrayList<>();

        for (CategoryEntity temp :list) {
            nameList.add(temp.getLabel());
        }
        boolean flag = true;
        for (String  tempName : nameList) {
            if(tempName.equals(name)) {
                flag = false;
                break;
            }
        }
        return Result.success("200",flag); // 判断文件名是否重复
    }

    @GetMapping("/inspectionOfIsCommon")
    public Result inspectionOfIsCommon(@RequestParam("newname") String name){
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", 0);
        wrapper.eq("is_common", 1);
        List<CategoryEntity> list = categoryService.list(wrapper);
        List<String>  nameList = new ArrayList<>();

        for (CategoryEntity temp :list) {
            nameList.add(temp.getLabel());
        }
        boolean flag = true;
        for (String  tempName : nameList) {
            if(tempName.equals(name)) {
                flag = false;
                break;
            }
        }
        return Result.success("200",flag); // 判断文件名是否重复
    }

}
