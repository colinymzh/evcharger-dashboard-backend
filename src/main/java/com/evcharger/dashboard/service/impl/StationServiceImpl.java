package com.evcharger.dashboard.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evcharger.dashboard.entity.Station;
import com.evcharger.dashboard.entity.StationSiteDTO;
import com.evcharger.dashboard.mapper.StationMapper;
import com.evcharger.dashboard.service.StationService;
import org.springframework.stereotype.Service;

@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements StationService {

    @Override
    public IPage<StationSiteDTO> getStationsWithSite(Page<?> page) {
        return baseMapper.selectStationWithSite(page);
    }
}
