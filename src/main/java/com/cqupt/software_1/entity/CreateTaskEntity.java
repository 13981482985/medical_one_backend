package com.cqupt.software_1.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateTaskEntity {
    private String principal;
    private String participants;
    private String taskName;
    private String tasktype;
    private String tips;
}
