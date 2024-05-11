package com.cqupt.software_1.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@TableName("category")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryEntity {
    @TableId
    private String id;
    private Integer catLevel;
    private String label;
    private String parentId;
    private Integer isLeafs;
    private Integer isDelete;
    private String uid;
    private String status;
    private String username;
    private String is_filter;
    private String is_upload;
    private String icdCode;
    @TableField(exist = false)
    private List<CategoryEntity> children;

    //    疾病管理新增
    @TableField(exist = false)
    private int tableNum0;
    @TableField(exist = false)
    private int tableNum1;
    @TableField(exist = false)
    private int tableNum2;

    private String uidList;

    private Integer isCommon;
    private Integer isWideTable;
    private String path;

    public CategoryEntity(String id, Integer catLevel, String label, String parentId, Integer isLeafs, Integer isDelete,
                          String uid, String status, String username, String is_upload, String is_filter){
        /**
         * node.getId(), node.getCatLevel(), node.getLabel(), node.getParentId(), node.getIsLeafs(),
         *                     node.getIsDelete(), node.getUid(), "0", node.getUsername(),node.getIs_upload(),node.getIs_filter()
         */
        this.id = id;
        this.catLevel = catLevel;
        this.label = label;
        this.parentId = parentId;
        this.isLeafs = isLeafs;
        this.isDelete = isDelete;
        this.uid = uid;
        this.status = status;
        this.username = username;
        this.is_filter = is_filter;
        this.is_upload = is_upload;

    }
    public CategoryEntity(String id, Integer catLevel, String label, String parentId, Integer isLeafs, Integer isCommon, String path, Integer isDelete, Integer isWideTable, List<CategoryEntity> children) {
        this.id = id;
        this.catLevel = catLevel;
        this.label = label;
        this.parentId = parentId;
        this.isLeafs = isLeafs;
        this.isCommon = isCommon;
        this.path = path;
        this.isDelete = isDelete;
        this.isWideTable = isWideTable;
        this.children = children;
    }

    public CategoryEntity(String id, Integer catLevel, String label, String parentId, Integer isLeafs, Integer isDelete, String uid, String status, String username, String is_filter, String is_upload, String icdCode, List<CategoryEntity> children, int tableNum0, int tableNum1, int tableNum2) {
        this.id = id;
        this.catLevel = catLevel;
        this.label = label;
        this.parentId = parentId;
        this.isLeafs = isLeafs;
        this.isDelete = isDelete;
        this.uid = uid;
        this.status = status;
        this.username = username;
        this.is_filter = is_filter;
        this.is_upload = is_upload;
        this.icdCode = icdCode;
        this.children = children;
        this.tableNum0 = tableNum0;
        this.tableNum1 = tableNum1;
        this.tableNum2 = tableNum2;
    }

    public CategoryEntity(String id, Integer catLevel, String label, String parentId, Integer isLeafs, Integer isDelete, String uid, String status, String username, String isUpload, String isFilter, String icdCode, String uidList) {
        this.id = id;
        this.catLevel  =catLevel;
        this.label = label;
        this.parentId = parentId;
        this.isLeafs = isLeafs;
        this.isDelete = isDelete;
        this.uid = uid;
        this.username = username;
        this.status = status;
        this.is_upload = isUpload;
        this.is_filter = isFilter;
        this.uidList = uidList;
        this.icdCode = icdCode;
    }

    public void addChild(CategoryEntity child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }


}
