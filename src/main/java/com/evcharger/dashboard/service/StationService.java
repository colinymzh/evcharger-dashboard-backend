package com.evcharger.dashboard.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.evcharger.dashboard.entity.Station;
import com.evcharger.dashboard.entity.StationSiteDTO;

public interface StationService extends IService<Station> {

    IPage<StationSiteDTO> getStationsWithSite(Page<?> page);
}
