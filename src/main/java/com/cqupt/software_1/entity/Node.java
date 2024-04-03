package com.cqupt.software_1.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Node implements Serializable {

    private Integer id;
    private String path;
    private String label;
    private boolean isLeafs;
    private boolean isCommon;
    private List<Node> children;
}
