package com.evcharger.dashboard.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class CityUsageResponseDTO {
    private String cityName;
    private List<WeeklyCityUsageDTO> weeklyUsage;
}
