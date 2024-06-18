package com.evcharger.dashboard.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evcharger.dashboard.entity.ConnectorDTO;
import com.evcharger.dashboard.entity.Station;
import com.evcharger.dashboard.entity.StationDetailDTO;
import com.evcharger.dashboard.entity.StationSiteDTO;
import com.evcharger.dashboard.mapper.ConnectorMapper;
import com.evcharger.dashboard.mapper.StationMapper;
import com.evcharger.dashboard.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements StationService {

    @Override
    public IPage<StationSiteDTO> getStationsWithSite(Page<?> page) {
        return baseMapper.selectStationWithSite(page);
    }

    @Override
    public IPage<StationSiteDTO> getStationsWithFilters(Page<?> page, String stationName, String city, String postcode, Boolean supportsFastCharging) {
        return baseMapper.selectStationWithFilters(page, stationName, city, postcode, supportsFastCharging);
    }

    @Autowired
    private ConnectorMapper connectorMapper;

    @Override
    public StationDetailDTO getStationDetails(String stationName) {
        StationDetailDTO stationDetail = baseMapper.getStationDetails(stationName);
        if (stationDetail != null) {
            List<ConnectorDTO> connectors = connectorMapper.getConnectorsByStationName(stationName);
            stationDetail.setConnectors(connectors);
        }
        return stationDetail;
    }
}
