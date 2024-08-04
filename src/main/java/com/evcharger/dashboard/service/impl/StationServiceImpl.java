package com.evcharger.dashboard.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evcharger.dashboard.entity.*;
import com.evcharger.dashboard.entity.dto.ConnectorDTO;
import com.evcharger.dashboard.entity.dto.StationDetailDTO;
import com.evcharger.dashboard.entity.dto.StationSiteDTO;
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

    /**
     * Retrieves a paginated list of stations along with their site information.
     *
     * @param page Pagination details.
     * @return A page of StationSiteDTO containing station and site information.
     */
    @Override
    public IPage<StationSiteDTO> getStationsWithSite(Page<?> page) {
        return baseMapper.selectStationWithSite(page);
    }

//    @Override
//    public IPage<StationSiteDTO> getStationsWithFilters(Page<?> page, String stationName, String city, String postcode, Boolean supportsFastCharging) {
//        return baseMapper.selectStationWithFilters(page, stationName, city, postcode, supportsFastCharging);
//    }

    /**
     * Retrieves a paginated list of stations filtered by various parameters.
     *
     * @param page                Pagination details.
     * @param stationName         Station name filter.
     * @param city                City filter.
     * @param postcode            Postcode filter.
     * @param supportsFastCharging Filter for fast-charging support.
     * @return A page of StationSiteDTO containing filtered station and site information.
     */
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

    /**
     * Retrieves detailed information about a specific station.
     *
     * @param stationName The name of the station.
     * @return A StationDetailDTO containing detailed station information, including connectors and city name.
     */
    @Override
    public StationDetailDTO getStationDetails(String stationName) {
        // Retrieve basic station details
        StationDetailDTO stationDetail = baseMapper.getStationDetails(stationName);
        if (stationDetail != null) {
            // Retrieve connectors associated with the station
            List<ConnectorDTO> connectors = connectorMapper.getConnectorsByStationName(stationName);
            stationDetail.setConnectors(connectors);
            // Retrieve city name associated with the station
            String cityName = cityMapper.getCityNameById(stationDetail.getCityId());
            stationDetail.setCityName(cityName);
        }
        return stationDetail;
    }

    /**
     * Retrieves usage statistics for stations over the last week, including location information.
     *
     * @return A list of StationUsageDTO containing usage statistics and location information.
     */
    @Override
    public List<StationUsageDTO> getStationsUsageWithLocation() {
        // 1. Get the latest date
        LocalDate latestDate = availabilityMapper.getLatestDate();
        if (latestDate == null) {
            return new ArrayList<>();
        }

        // 2. Calculation of the start date (7 days before the latest date)
        LocalDate startDate = latestDate.minusDays(6);

        // 3. Obtaining usage data for a specified date range
        List<StationUsage> stationUsages = availabilityMapper.getStationUsageForLastWeek(startDate, latestDate);

        if (stationUsages.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. Calculation of usage rates and grades
        List<StationUsageDTO> result = stationUsages.stream()
                .map(this::calculateUsageAndLevel)
                .collect(Collectors.toList());

        // 5. Access to location information
        addLocationInfo(result);

        return result;
    }

    /**
     * Calculates usage statistics and level for a given station usage record.
     *
     * @param usage The station usage record.
     * @return A StationUsageDTO containing calculated usage statistics and level.
     */
    private StationUsageDTO calculateUsageAndLevel(StationUsage usage) {
        StationUsageDTO dto = new StationUsageDTO();
        dto.setStationName(usage.getStationName());
        dto.setAverageUsage(usage.getUnavailableCount().doubleValue() / usage.getTotalCount());
        dto.setUsageLevel(calculateUsageLevel(dto.getAverageUsage()));
        return dto;
    }

    /**
     * Calculates the usage level based on average usage.
     *
     * @param averageUsage The average usage rate.
     * @return The usage level as an integer.
     */
    private Integer calculateUsageLevel(Double averageUsage) {
        if (averageUsage < 0.25) return 1;
        if (averageUsage < 0.5) return 2;
        if (averageUsage < 0.75) return 3;
        return 4;
    }

    /**
     * Adds location information to the list of station usage statistics.
     *
     * @param stationUsages The list of station usage statistics.
     */
    private void addLocationInfo(List<StationUsageDTO> stationUsages) {
        // Retrieve station names
        List<String> stationNames = stationUsages.stream()
                .map(StationUsageDTO::getStationName)
                .collect(Collectors.toList());

        // Retrieve location information for stations
        List<StationLocation> locationList = stationMapper.getStationLocations(stationNames);

        // Map station names to their locations
        Map<String, StationLocation> locationMap = locationList.stream()
                .collect(Collectors.toMap(StationLocation::getStationName, location -> location));

        // Add location coordinates to each station usage record
        stationUsages.forEach(usage -> {
            StationLocation location = locationMap.get(usage.getStationName());
            if (location != null) {
                usage.setCoordinatesX(location.getCoordinatesX());
                usage.setCoordinatesY(location.getCoordinatesY());
            }
        });
    }
}
