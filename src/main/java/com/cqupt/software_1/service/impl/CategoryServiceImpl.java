package com.cqupt.software_1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software_1.common.Result;
import com.cqupt.software_1.common.UserThreadLocal;
import com.cqupt.software_1.entity.CategoryEntity;
import com.cqupt.software_1.entity.UserLog;
import com.cqupt.software_1.mapper.CategoryMapper;
import com.cqupt.software_1.service.CategoryService;
import com.cqupt.software_1.service.UserLogService;
import com.cqupt.software_1.vo.AddDiseaseVo;
import com.cqupt.software_1.vo.UpdateDiseaseVo;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

// TODO 公共模块新增类

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity>
        implements CategoryService {

    @Autowired
    CategoryMapper dataManagerMapper;
    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    UserLogService userLogService;
    @Override
    public List<CategoryEntity> getCategory() {

        // 获取所有目录行程树形结构
        List<CategoryEntity> categoryEntities = dataManagerMapper.selectList(null);
        // 获取所有级结构
        List<CategoryEntity> treeData = categoryEntities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentId().equals("1") && categoryEntity.getIsDelete()==0 && categoryEntity.getCatLevel()==2;
        }).map((level1Cat) -> {
            level1Cat.setChildren(getCatChildren(level1Cat, categoryEntities));;
            return level1Cat;
        }).collect(Collectors.toList());

        CategoryEntity pu = null;
        for (CategoryEntity notPublicDatum : treeData) {
            if(notPublicDatum.getIsCommon()!=null && notPublicDatum.getIsCommon()==1) pu = notPublicDatum;
        }
        if(pu!=null){
            treeData.remove(pu);
            treeData.add(pu);
        }
        System.out.println("返回数据为："+treeData);
        return treeData;
    }


    // TODO 新修改
    @Override
    public List<CategoryEntity> getCategory(Integer uid) {
        // 获取所有目录行程树形结构
        List<CategoryEntity> categoryEntities = categoryMapper.selectList(null);
        // 获取所有级结构
        List<CategoryEntity> treeData = categoryEntities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentId().equals("0") && categoryEntity.getIsDelete() == 0;
        }).map((level1Cat) -> {
            level1Cat.setChildren(getCatChildren(level1Cat, categoryEntities));
            return level1Cat;
        }).collect(Collectors.toList());


        CategoryEntity copiedTree1 = copyPrivareTreeStructure(treeData.get(0), uid.toString());
        copiedTree1.setLabel("私有数据集");
        CategoryEntity copiedTree2 = copyShareTreeStructure(treeData.get(0), uid.toString());
        copiedTree2.setLabel("共享数据集");
        CategoryEntity copiedTree3 = copyCommonTreeStructure(treeData.get(0));
        copiedTree3.setLabel("公共数据集");
        List<CategoryEntity> res = new ArrayList<CategoryEntity>();
        res.add(copiedTree1);
        res.add(copiedTree2);
        res.add(copiedTree3);
        return res;
    }


    // 递归复制符合条件的节点
    public static CategoryEntity copyPrivareTreeStructure(CategoryEntity node,String uid) {
        if (node.getIsLeafs() == 0 || (node.getIsLeafs() == 1 && "0".equals(node.getStatus()) && uid.equals(node.getUid()))) {
            CategoryEntity newNode = new CategoryEntity(node.getId(), node.getCatLevel(), node.getLabel(), node.getParentId(),
                    node.getIsLeafs(), node.getIsDelete(), node.getUid(), "0", node.getUsername(),node.getIs_upload(),node.getIs_filter(),
                    node.getIcdCode(),node.getUidList());
            if (node.getChildren() != null) {
                for (CategoryEntity child : node.getChildren()) {
                    CategoryEntity copiedChild = copyPrivareTreeStructure(child,uid);
                    if (copiedChild != null) {
                        newNode.addChild(copiedChild);
                    }
                }
            }
            return newNode;
        } else {
            return null;
        }
    }
    public static CategoryEntity copyShareTreeStructure(CategoryEntity node,String uid) {
        if (node.getIsLeafs() == 0 || (node.getIsLeafs() == 1 && "1".equals(node.getStatus()) && (node.getUidList().contains(uid)||uid.equals(node.getUid())) )) {
            CategoryEntity newNode = new CategoryEntity(node.getId(), node.getCatLevel(), node.getLabel(), node.getParentId(),
                    node.getIsLeafs(), node.getIsDelete(), node.getUid(), "1", node.getUsername(),node.getIs_upload(),node.getIs_filter(),node.getIcdCode(),node.getUidList());
            if (node.getChildren() != null) {
                for (CategoryEntity child : node.getChildren()) {
                    CategoryEntity copiedChild = copyShareTreeStructure(child,uid);
                    if (copiedChild != null) {
                        newNode.addChild(copiedChild);
                    }
                }
            }
            return newNode;
        } else {
            return null;
        }
    }

//    public List<CategoryEntity> getCategory(Integer uid) {
//        List<CategoryEntity> categoryEntities_private = new ArrayList<CategoryEntity>();
//        List<CategoryEntity> categoryEntities_share = new ArrayList<CategoryEntity>();
//        List<CategoryEntity> categoryEntities_common = new ArrayList<CategoryEntity>();
//        // 获取所有目录行程树形结构
//        List<CategoryEntity> categoryEntities = categoryMapper.selectList(null);
//        // 获取所有级结构
//        List<CategoryEntity> treeData = categoryEntities.stream().filter((categoryEntity) -> {
//            return categoryEntity.getParentId().equals("0") && categoryEntity.getIsDelete()==0;
//        }).map((level1Cat) -> {
//            level1Cat.setChildren(getCatChildren(level1Cat, categoryEntities));
//            return level1Cat;
//        }).collect(Collectors.toList());
//        CategoryEntity copiedTree1 = copyPrivareTreeStructure(treeData.get(0),uid.toString());
//        copiedTree1.setLabel("私有数据集");
//        CategoryEntity copiedTree2 = copyShareTreeStructure(treeData.get(0));
//        copiedTree2.setLabel("共享数据集");
//        CategoryEntity copiedTree3 = copyCommonTreeStructure(treeData.get(0));
//        copiedTree3.setLabel("公共数据集");
//        List<CategoryEntity> res = new ArrayList<CategoryEntity>();
//        res.add(copiedTree1);
//        res.add(copiedTree2);
//        res.add(copiedTree3);
//        return res;
//    }

//    public static CategoryEntity copyPrivareTreeStructure(CategoryEntity node,String uid) {
//        if (node.getIsLeafs() == 0 || (node.getIsLeafs() == 1 && "0".equals(node.getStatus()) && uid.equals(node.getUid()))) {
//            CategoryEntity newNode = new CategoryEntity(node.getId(), node.getCatLevel(), node.getLabel(), node.getParentId(), node.getIsLeafs(),
//                    node.getIsDelete(), node.getUid(), "0", node.getUsername(),node.getIs_upload(),node.getIs_filter());
//            if (node.getChildren() != null) {
//                for (CategoryEntity child : node.getChildren()) {
//                    CategoryEntity copiedChild = copyPrivareTreeStructure(child,uid);
//                    if (copiedChild != null) {
//                        newNode.addChild(copiedChild);
//                    }
//                }
//            }
//            return newNode;
//        } else {
//            return null;
//        }
//    }

//    public static CategoryEntity copyShareTreeStructure(CategoryEntity node) {
//        if (node.getIsLeafs() == 0 || (node.getIsLeafs() == 1 && "1".equals(node.getStatus()))) {
//            CategoryEntity newNode = new CategoryEntity(node.getId(), node.getCatLevel(), node.getLabel(), node.getParentId(), node.getIsLeafs(), node.getIsDelete(), node.getUid(), "1", node.getUsername(),node.getIs_upload(),node.getIs_filter());
//            if (node.getChildren() != null) {
//                for (CategoryEntity child : node.getChildren()) {
//                    CategoryEntity copiedChild = copyShareTreeStructure(child);
//                    if (copiedChild != null) {
//                        newNode.addChild(copiedChild);
//                    }
//                }
//            }
//            return newNode;
//        } else {
//            return null;
//        }
//    }

    public static CategoryEntity copyCommonTreeStructure(CategoryEntity node) {
        if (node.getIsLeafs() == 0 || (node.getIsLeafs() == 1 && "2".equals(node.getStatus()))) {
            CategoryEntity newNode = new CategoryEntity(node.getId(), node.getCatLevel(), node.getLabel(), node.getParentId(), node.getIsLeafs(), node.getIsDelete(), node.getUid(), "2", node.getUsername(),node.getIs_upload(),node.getIs_filter());
            if (node.getChildren() != null) {
                for (CategoryEntity child : node.getChildren()) {
                    CategoryEntity copiedChild = copyCommonTreeStructure(child);
                    if (copiedChild != null) {
                        newNode.addChild(copiedChild);
                    }
                }
            }
            return newNode;
        } else {
            return null;
        }
    }



    @Override
    public void removeNode(String id) {
        UserLog userLog = new UserLog();
        userLog.setOpTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        userLog.setUsername(UserThreadLocal.get().getUsername());
        userLog.setUid(UserThreadLocal.get().getUid());
        userLog.setOpType("删除病种数据信息");
        userLogService.save(userLog);
        categoryMapper.removeNode(id);
    }

    @Override
    public void addParentDisease(String diseaseName) {
        CategoryEntity categoryEntity = new CategoryEntity(null, 1, diseaseName, "0", 0, 0, "" + diseaseName, 0, 0, null);
        UserLog userLog = new UserLog();
        userLog.setOpTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        userLog.setUsername(UserThreadLocal.get().getUsername());
        userLog.setUid(UserThreadLocal.get().getUid());
        userLog.setOpType("添加新病种");
        userLogService.save(userLog);
        categoryMapper.insert(categoryEntity);
    }


    @Override
    public void changeStatus(CategoryEntity categoryEntity) {
        System.out.println(categoryEntity.getStatus());
        if (categoryEntity.getStatus().equals("0")){
            categoryMapper.changeStatusToShare(categoryEntity.getId());
        }
        else if(categoryEntity.getStatus().equals("1")){
            categoryMapper.changeStatusToPrivate(categoryEntity.getId());
        }

    }

    // 获取1级目录下的所有子结构
    private List<CategoryEntity> getCatChildren(CategoryEntity level1Cat, List<CategoryEntity> categoryEntities) {
        List<CategoryEntity> children = categoryEntities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentId().equals(level1Cat.getId()) && categoryEntity.getIsDelete()==0; // 获取当前分类的所有子分类
        }).map((child) -> {
            // 递归设置子分类的所有子分类
            child.setChildren(getCatChildren(child, categoryEntities));
            return child;
        }).collect(Collectors.toList());
        return children;
    }


    /**
     *
     *
     *   合并
     */

    //添加一级病种
    @Override
    public int addCategory(AddDiseaseVo addDiseaseVo){

        // 查询父节点 根据父节点设置当前节点信息
        CategoryEntity parent = categoryMapper.selectOne(new QueryWrapper<CategoryEntity>().eq("id", addDiseaseVo.getParentId()));

        CategoryEntity category = new CategoryEntity(null,parent.getCatLevel()+1,addDiseaseVo.getFirstDisease(),addDiseaseVo.getParentId(),0,0,addDiseaseVo.getUid(),null,addDiseaseVo.getUsername(),null,null,addDiseaseVo.getIcdCode(),null,0,0,0);
        return categoryMapper.insert(category);
    }

    //添加疾病管理模块
    /**
     * 修改
     * @return
     */
    @Override
    public List<CategoryEntity> getAllDisease(){
        // 获取所有目录行程树形结构
        List<CategoryEntity> categoryEntities = dataManagerMapper.selectList(null);
        // 获取所有级结构
        List<CategoryEntity> treeData = categoryEntities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentId().equals("1") && categoryEntity.getIsDelete()==0 && categoryEntity.getStatus()==null;
        }).map((level1Cat) -> {
            Pair<List<CategoryEntity>,int[]> pair = getDiseaseChildren(level1Cat,categoryEntities);
            level1Cat.setChildren(pair.getKey());
            //计算第二层目录下表数量和加上第二层表数量作为第一层的num
            int[] numsCat = pair.getValue();
            int[] numsTab = getFileNums(level1Cat.getId());
            level1Cat.setTableNum0(numsCat[0]+numsTab[0]);
            level1Cat.setTableNum1(numsCat[1]+numsTab[1]);
            level1Cat.setTableNum2(numsCat[2]+numsTab[2]);
            return level1Cat;
        }).collect(Collectors.toList());

        return treeData;
    }

    /**
     * 新增
     */
    private Pair<List<CategoryEntity>,int[]> getDiseaseChildren(CategoryEntity level1Cat, List<CategoryEntity> categoryEntities) {
        List<CategoryEntity> children = categoryEntities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentId().equals(level1Cat.getId()) && categoryEntity.getIsDelete()==0 && categoryEntity.getStatus()==null; // 获取当前分类的所有子分类
        }).map((child) -> {
            // 递归设置子分类的所有子分类
//            child.setChildren(getCatChildren(child, categoryEntities));
            Pair<List<CategoryEntity>,int[]> pair = getDiseaseChildren(child, categoryEntities);
            child.setChildren(pair.getKey());
            //计算第三层目录下表数量和加上第三层表数量作为第二层的num
            int[] numsCat = pair.getValue();
            int[] numsTab = getFileNums(child.getId());
            child.setTableNum0(numsCat[0]+numsTab[0]);
            child.setTableNum1(numsCat[1]+numsTab[1]);
            child.setTableNum2(numsCat[2]+numsTab[2]);

            return child;
        }).collect(Collectors.toList());
        int[] nums = new int[3];
        for(CategoryEntity category:children){
            nums[0] += category.getTableNum0();
            nums[1] += category.getTableNum1();
            nums[2] += category.getTableNum2();
        }
        return new Pair<>(children,nums);
    }

    // 获取第二层目录
    private Pair<List<CategoryEntity>,int[]> getSecondLevelChildren(String parentId) {
        // 获取所有第二层目录
        List<CategoryEntity> secondLevelCategories = dataManagerMapper.selectList(null).stream()
                .filter(categoryEntity -> categoryEntity.getParentId().equals(parentId) && categoryEntity.getIsDelete() == 0)
                .map(level2Cat -> {
                    int[] nums = getFileNums(level2Cat.getId());
                    level2Cat.setTableNum0(nums[0]);
                    level2Cat.setTableNum1(nums[1]);
                    level2Cat.setTableNum2(nums[2]);

                    level2Cat.setChildren(new ArrayList<>()); // 第二层目录没有子目录
                    return level2Cat;
                })
                .collect(Collectors.toList());
        //遍历该父节点下所有目录，获取该节点下表的总和
        int[] nums = new int[3];
        for(CategoryEntity category:secondLevelCategories){
            nums[0] += category.getTableNum0();
            nums[1] += category.getTableNum1();
            nums[2] += category.getTableNum2();
        }
        return new Pair<>(secondLevelCategories,nums);
    }


    private int[] getFileNums(String id){
        int[] nums = new int[3];
        List<CategoryEntity> secondLevelCategories = dataManagerMapper.selectList(null);
        for(CategoryEntity category:secondLevelCategories){
            if(category.getParentId().equals(id) && category.getIsDelete() == 0 && category.getStatus()!=null) {
                if (category.getStatus().equals("0"))
                    nums[0]++;
                else if (category.getStatus().equals("1"))
                    nums[1]++;
                else if (category.getStatus().equals("2"))
                    nums[2]++;
            }
        }
        return nums;
    }

    //添加病种
    //添加一级病种
//    @Override
//    public int addCategory(AddDiseaseVo addDiseaseVo){
//        CategoryEntity category = new CategoryEntity(null,1,addDiseaseVo.getFirstDisease(),addDiseaseVo.getParentId(),0,0,addDiseaseVo.getUid(),null,addDiseaseVo.getUsername(),null,null,addDiseaseVo.getIcdCode(),null,0,0,0);
//        return categoryMapper.insert(category);
//    }
//
//    private Result addFirstDisease(AddDiseaseVo addDiseaseVo){
//        //判断该病种是否已经存在
//        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("parent_id", "0")
//                .eq("label", addDiseaseVo.getFirstDisease())
//                .eq("is_delete",0);
//        CategoryEntity category = categoryMapper.selectOne(queryWrapper);
//        if(category==null){
//            CategoryEntity categoryEntity = new CategoryEntity(null, 1, addDiseaseVo.getFirstDisease(), "0", 0, 0, addDiseaseVo.getUid(), null, addDiseaseVo.getUsername(), null,null,null,0,0,0);
//            categoryMapper.insert(categoryEntity);
//            return Result.success("添加成功");
//        }else{
//            return Result.fail("该一级病种已经存在");
//        }
//    }
//
//
//    private Result addSecondDisease(AddDiseaseVo addDiseaseVo){
//        //获取一级病种，若不存在则插入,并取得一级病种id
//        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("parent_id", "0")
//                .eq("label", addDiseaseVo.getFirstDisease())
//                .eq("is_delete",0);
//        CategoryEntity category = categoryMapper.selectOne(queryWrapper);
//        String categoryId;
//        if(category==null){
//            System.out.println("该一级目录不存在，已添加");
//            CategoryEntity categoryEntity = new CategoryEntity(null, 1, addDiseaseVo.getFirstDisease(), "0", 0, 0, addDiseaseVo.getUid(), null, addDiseaseVo.getUsername(),null,null, null,0,0,0);
//            categoryMapper.insert(categoryEntity);
//            QueryWrapper<CategoryEntity> queryWrapper1 = new QueryWrapper<>();
//            queryWrapper.eq("parent_id", "0")
//                    .eq("label", addDiseaseVo.getFirstDisease())
//                    .eq("is_delete",0);
//            CategoryEntity category1 = categoryMapper.selectOne(queryWrapper);
//            categoryId = category1.getId();
//        }else{
//            System.out.println("该一级目录存在");
//            categoryId = category.getId();
//        }
//        //获取二级病种，若不存在则插入
//        QueryWrapper<CategoryEntity> queryWrapper2 = new QueryWrapper<>();
//        queryWrapper2.eq("parent_id", categoryId)
//                .eq("label", addDiseaseVo.getSecondDisease())
//                .eq("is_delete",0);
//        CategoryEntity category2 = categoryMapper.selectOne(queryWrapper2);
//        System.out.println(category2);
//        if(category2==null){
//            CategoryEntity categoryEntity2 = new CategoryEntity(null, 2, addDiseaseVo.getSecondDisease(), categoryId, 0, 0, addDiseaseVo.getUid(), null, addDiseaseVo.getUsername(), null,null,null,0,0,0);
//            categoryMapper.insert(categoryEntity2);
//            System.out.println("Chenggong");
//            return Result.success("添加成功");
//        }else{
//            return Result.fail("该二级病种已经存在");
//        }
//    }

    @Override
    public Result updateCategory(UpdateDiseaseVo updateDiseaseVo){
        UpdateWrapper<CategoryEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", updateDiseaseVo.getCategoryId())
                .set("label", updateDiseaseVo.getDiseaseName())
                .set("parent_id", updateDiseaseVo.getParentId())
                .set("uid", updateDiseaseVo.getUid())
                .set("username", updateDiseaseVo.getUsername())
                .set("icd_code",updateDiseaseVo.getIcdCode());
        return categoryMapper.update(null, updateWrapper)>0?Result.success("插入成功"):Result.fail("插入失败");
    }

    @Override
    public void removeCategorys(List<String> deleteIds){
        for(String id:deleteIds){
            UpdateWrapper<CategoryEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", id)
                    .set("is_delete", 1);
            categoryMapper.update(null, updateWrapper);
        }
    }





    //    下面方法是管理员端-数据管理新增
    @Override
    public List<CategoryEntity> getLevel2Label() {
        return categoryMapper.getLevel2Label();
    }

    @Override
    public List<CategoryEntity> getLabelsByPid(String pid) {
        return categoryMapper.getLabelsByPid(pid);
    }
    public void updateTableNameByTableId(String tableid, String tableName, String tableStatus) {
        System.out.println("status: " + tableStatus);
        categoryMapper.updateTableNameByTableId(tableid, tableName, tableStatus);
    }

}
