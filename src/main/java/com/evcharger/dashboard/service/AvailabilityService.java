package com.evcharger.dashboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.evcharger.dashboard.entity.Availability;
import com.evcharger.dashboard.entity.dto.*;

import java.util.List;

public interface AvailabilityService extends IService<Availability> {
    List<Availability> getAvailabilityByStationAndDate(String stationName, String date);

    //Point in Time, Availability Maps
    List<ConnectorUsageResponseDTO> getConnectorUsageByStationAndScope(String stationName, int scope);
    //Point in Time, Availability Maps
    List<ConnectorUsageResponseDTO> getConnectorUsageByTimePeriod(String stationName, int scope);

    // Days of the week, availability charts
    List<ConnectorWeeklyUsageResponseDTO> getWeeklyUsageByStationAndConnector(String stationName);

    CityUsageResponseDTO getWeeklyUsageByCity(String stationName);


    List<ConnectorWeeklyHourlyUsageDTO> getWeeklyHourlyUsageByStationAndConnector(String stationName);

}
