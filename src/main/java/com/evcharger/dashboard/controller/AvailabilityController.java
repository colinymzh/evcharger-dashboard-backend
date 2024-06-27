package com.evcharger.dashboard.controller;

import com.evcharger.dashboard.entity.Availability;
import com.evcharger.dashboard.entity.dto.ConnectorUsageResponseDTO;
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

    @GetMapping("/station/usage")
    public List<ConnectorUsageResponseDTO> getConnectorUsageByStationAndScope(@RequestParam("stationName") String stationName,
                                                                              @RequestParam("scope") int scope) {
        return availabilityService.getConnectorUsageByStationAndScope(stationName, scope);
    }

    @GetMapping("/station/usage/time-period")
    public List<ConnectorUsageResponseDTO> getConnectorUsageByTimePeriod(@RequestParam("stationName") String stationName,
                                                                         @RequestParam("scope") int scope) {
        return availabilityService.getConnectorUsageByTimePeriod(stationName, scope);
    }

}
