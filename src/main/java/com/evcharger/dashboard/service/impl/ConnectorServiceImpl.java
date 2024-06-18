package com.evcharger.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evcharger.dashboard.entity.Connector;
import com.evcharger.dashboard.mapper.ConnectorMapper;
import com.evcharger.dashboard.service.ConnectorService;
import org.springframework.stereotype.Service;

@Service
public class ConnectorServiceImpl extends ServiceImpl<ConnectorMapper, Connector> implements ConnectorService {
}
