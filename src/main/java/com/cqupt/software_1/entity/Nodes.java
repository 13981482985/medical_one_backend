package com.cqupt.software_1.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value ="nodes",schema = "public")
public class Nodes {
    @TableId
    private int id;

    @Type(type = "jsonb") // 使用 jsonb 类型来存储 JSON 数据
    @Column(columnDefinition = "jsonb")
    private String data;



    public static JsonNode parseJsonString(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
