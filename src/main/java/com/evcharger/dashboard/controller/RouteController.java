package com.evcharger.dashboard.controller;

import com.evcharger.dashboard.entity.dto.RouteSummaryDTO;
import com.evcharger.dashboard.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/summary")
    public RouteSummaryDTO getRouteSummary(@RequestParam("startX") double startX,
                                           @RequestParam("startY") double startY,
                                           @RequestParam("stationName") String stationName) {
        return routeService.getRouteSummary(startX, startY, stationName);
    }
}
