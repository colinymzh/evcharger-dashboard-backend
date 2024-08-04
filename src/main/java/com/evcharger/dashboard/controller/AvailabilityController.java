package com.evcharger.dashboard.controller;

import com.evcharger.dashboard.entity.Availability;
import com.evcharger.dashboard.entity.dto.*;
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

    //Point in time, utilisation rate, line graphs
    @GetMapping("/station/usage")
    public List<ConnectorUsageResponseDTO> getConnectorUsageByStationAndScope(@RequestParam("stationName") String stationName,
                                                                              @RequestParam("scope") int scope) {
        return availabilityService.getConnectorUsageByStationAndScope(stationName, scope);
    }

    // Time period, utilisation rate, line graphs
    @GetMapping("/station/usage/time-period")
    public List<ConnectorUsageResponseDTO> getConnectorUsageByTimePeriod(@RequestParam("stationName") String stationName,
                                                                         @RequestParam("scope") int scope) {
        return availabilityService.getConnectorUsageByTimePeriod(stationName, scope);
    }

    //Day of week, utilisation rate, bar charts

    @GetMapping("/station/weekly-usage")
    public List<ConnectorWeeklyUsageResponseDTO> getWeeklyUsageByStationAndConnector(@RequestParam("stationName") String stationName) {
        return availabilityService.getWeeklyUsageByStationAndConnector(stationName);
    }

    //city, day of the week, bar graphs
    @GetMapping("/city/weekly-usage")
    public CityUsageResponseDTO getWeeklyUsageByCity(@RequestParam("stationName") String stationName) {
        return availabilityService.getWeeklyUsageByCity(stationName);
    }

    //site, weekly, hourly, heat map
    @GetMapping("/station/weekly-hourly-usage")
    public List<ConnectorWeeklyHourlyUsageDTO> getWeeklyHourlyUsageByStationAndConnector(@RequestParam("stationName") String stationName) {
        return availabilityService.getWeeklyHourlyUsageByStationAndConnector(stationName);
    }


}
