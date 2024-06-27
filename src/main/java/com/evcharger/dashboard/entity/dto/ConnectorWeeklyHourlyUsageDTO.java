package com.evcharger.dashboard.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class ConnectorWeeklyHourlyUsageDTO {
    private String connectorId;
    private List<WeeklyHourlyUsageDTO> weeklyHourlyUsage;
}
