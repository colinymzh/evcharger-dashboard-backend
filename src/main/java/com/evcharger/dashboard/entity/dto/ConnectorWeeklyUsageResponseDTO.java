package com.evcharger.dashboard.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class ConnectorWeeklyUsageResponseDTO {
    private String connectorId;
    private List<WeeklyUsageDTO> weeklyUsage;
}
