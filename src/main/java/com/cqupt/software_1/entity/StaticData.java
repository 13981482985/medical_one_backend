package com.cqupt.software_1.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.springframework.data.auditing.IsNewAwareAuditingHandler;

@TableName("static_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaticData {
    private Long allCount;
    private Long allIndex;
    private Float allMissRate;
    private Float allValidRate;
}
