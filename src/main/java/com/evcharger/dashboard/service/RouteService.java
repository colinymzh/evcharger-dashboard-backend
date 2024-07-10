package com.evcharger.dashboard.service;

import com.evcharger.dashboard.entity.dto.RouteSummaryDTO;

public interface RouteService {
    RouteSummaryDTO getRouteSummary(double startX, double startY, String stationName);
}
