package com.evcharger.dashboard.controller;

import com.evcharger.dashboard.entity.Availability;
import com.evcharger.dashboard.entity.dto.CityUsageResponseDTO;
import com.evcharger.dashboard.entity.dto.ConnectorUsageResponseDTO;
import com.evcharger.dashboard.entity.dto.ConnectorWeeklyUsageResponseDTO;
import com.evcharger.dashboard.entity.dto.WeeklyUsageDTO;
import com.evcharger.dashboard.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/availability")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    @GetMapping("/station")
    public List<Availability> getAvailabilityByStationAndDate(@RequestParam("stationName") String stationName,
                                                              @RequestParam("date") String date) {
        return availabilityService.getAvailabilityByStationAndDate(stationName, date);
    }

    //时间点、使用率、折线图
    @GetMapping("/station/usage")
    public List<ConnectorUsageResponseDTO> getConnectorUsageByStationAndScope(@RequestParam("stationName") String stationName,
                                                                              @RequestParam("scope") int scope) {
        return availabilityService.getConnectorUsageByStationAndScope(stationName, scope);
    }

    //时间段、使用率、折线图
    @GetMapping("/station/usage/time-period")
    public List<ConnectorUsageResponseDTO> getConnectorUsageByTimePeriod(@RequestParam("stationName") String stationName,
                                                                         @RequestParam("scope") int scope) {
        return availabilityService.getConnectorUsageByTimePeriod(stationName, scope);
    }

    //星期几、使用率、条形图

    @GetMapping("/station/weekly-usage")
    public List<ConnectorWeeklyUsageResponseDTO> getWeeklyUsageByStationAndConnector(@RequestParam("stationName") String stationName) {
        return availabilityService.getWeeklyUsageByStationAndConnector(stationName);
    }

    @GetMapping("/city/weekly-usage")
    public CityUsageResponseDTO getWeeklyUsageByCity(@RequestParam("stationName") String stationName) {
        return availabilityService.getWeeklyUsageByCity(stationName);
    }
}
