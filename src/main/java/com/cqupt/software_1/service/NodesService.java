package com.cqupt.software_1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software_1.entity.Nodes;
import com.cqupt.software_1.entity.Relationships;

import java.util.List;

public interface NodesService extends IService<Nodes> {
    List<Nodes> getAllNodes();

    List<Relationships> getRelationships();
}
