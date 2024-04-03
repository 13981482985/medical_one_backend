package com.cqupt.software_1.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value ="relationships",schema = "public")
public class Relationships {
    private Integer parentId;
    private Integer childId;
}
