package com.cqupt.software_1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software_1.entity.Nodes;
import com.cqupt.software_1.entity.Relationships;
import com.cqupt.software_1.mapper.NodesMapper;
import com.cqupt.software_1.service.NodesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NodesServiceImpl extends ServiceImpl<NodesMapper, Nodes>
        implements NodesService {
    @Autowired
    NodesMapper nodesMapper;


    @Override
    public List<Nodes> getAllNodes() {
        return nodesMapper.getAllNodes();
    }

    @Override
    public List<Relationships> getRelationships() {
        return nodesMapper.getRelationships();
    }
}
