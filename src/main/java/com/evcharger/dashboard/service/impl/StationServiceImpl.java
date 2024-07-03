package com.evcharger.dashboard.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evcharger.dashboard.entity.*;
import com.evcharger.dashboard.entity.dto.StationUsageDTO;
import com.evcharger.dashboard.mapper.AvailabilityMapper;
import com.evcharger.dashboard.mapper.CityMapper;
import com.evcharger.dashboard.mapper.ConnectorMapper;
import com.evcharger.dashboard.mapper.StationMapper;
import com.evcharger.dashboard.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements StationService {

    @Override
    public IPage<StationSiteDTO> getStationsWithSite(Page<?> page) {
        return baseMapper.selectStationWithSite(page);
    }

//    @Override
//    public IPage<StationSiteDTO> getStationsWithFilters(Page<?> page, String stationName, String city, String postcode, Boolean supportsFastCharging) {
//        return baseMapper.selectStationWithFilters(page, stationName, city, postcode, supportsFastCharging);
//    }

    @Override
    public IPage<StationSiteDTO> getStationsWithFilters(Page<?> page, String stationName, String city, String postcode, Boolean supportsFastCharging) {
        return baseMapper.selectStationWithFilters(page, stationName, city, postcode, supportsFastCharging);
    }

    @Autowired
    private ConnectorMapper connectorMapper;

//    @Override
//    public StationDetailDTO getStationDetails(String stationName) {
//        StationDetailDTO stationDetail = baseMapper.getStationDetails(stationName);
//        if (stationDetail != null) {
//            List<ConnectorDTO> connectors = connectorMapper.getConnectorsByStationName(stationName);
//            stationDetail.setConnectors(connectors);
//        }
//        return stationDetail;
//    }


    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private AvailabilityMapper availabilityMapper;

    @Autowired
    private StationMapper stationMapper;

    @Override
    public StationDetailDTO getStationDetails(String stationName) {
        StationDetailDTO stationDetail = baseMapper.getStationDetails(stationName);
        if (stationDetail != null) {
            List<ConnectorDTO> connectors = connectorMapper.getConnectorsByStationName(stationName);
            stationDetail.setConnectors(connectors);
            String cityName = cityMapper.getCityNameById(stationDetail.getCityId());
            stationDetail.setCityName(cityName);
        }
        return stationDetail;
    }


    @Override
    public List<StationUsageDTO> getStationsUsageWithLocation() {
        // 1. 获取最新的日期
        LocalDate latestDate = availabilityMapper.getLatestDate();
        if (latestDate == null) {
            return new ArrayList<>();
        }

        // 2. 计算开始日期（最新日期往前推7天）
        LocalDate startDate = latestDate.minusDays(6);

        // 3. 获取指定日期范围内的使用率数据
        List<StationUsage> stationUsages = availabilityMapper.getStationUsageForLastWeek(startDate, latestDate);

        if (stationUsages.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. 计算使用率和等级
        List<StationUsageDTO> result = stationUsages.stream()
                .map(this::calculateUsageAndLevel)
                .collect(Collectors.toList());

        // 5. 获取位置信息
        addLocationInfo(result);

        return result;
    }

    private StationUsageDTO calculateUsageAndLevel(StationUsage usage) {
        StationUsageDTO dto = new StationUsageDTO();
        dto.setStationName(usage.getStationName());
        dto.setAverageUsage(usage.getUnavailableCount().doubleValue() / usage.getTotalCount());
        dto.setUsageLevel(calculateUsageLevel(dto.getAverageUsage()));
        return dto;
    }

    private Integer calculateUsageLevel(Double averageUsage) {
        if (averageUsage < 0.25) return 1;
        if (averageUsage < 0.5) return 2;
        if (averageUsage < 0.75) return 3;
        return 4;
    }

    private void addLocationInfo(List<StationUsageDTO> stationUsages) {
        List<String> stationNames = stationUsages.stream()
                .map(StationUsageDTO::getStationName)
                .collect(Collectors.toList());

        List<StationLocation> locationList = stationMapper.getStationLocations(stationNames);

        Map<String, StationLocation> locationMap = locationList.stream()
                .collect(Collectors.toMap(StationLocation::getStationName, location -> location));

        stationUsages.forEach(usage -> {
            StationLocation location = locationMap.get(usage.getStationName());
            if (location != null) {
                usage.setCoordinatesX(location.getCoordinatesX());
                usage.setCoordinatesY(location.getCoordinatesY());
            }
        });
    }
}
