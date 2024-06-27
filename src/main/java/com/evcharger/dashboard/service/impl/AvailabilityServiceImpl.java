package com.evcharger.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evcharger.dashboard.entity.Availability;
import com.evcharger.dashboard.entity.dto.ConnectorUsageDTO;
import com.evcharger.dashboard.entity.dto.ConnectorUsageResponseDTO;
import com.evcharger.dashboard.entity.dto.TimePeriodUsageDTO;
import com.evcharger.dashboard.mapper.AvailabilityMapper;
import com.evcharger.dashboard.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AvailabilityServiceImpl extends ServiceImpl<AvailabilityMapper, Availability> implements AvailabilityService {

    @Autowired
    private AvailabilityMapper availabilityMapper;
    @Override
    public List<Availability> getAvailabilityByStationAndDate(String stationName, String date) {
        return baseMapper.getAvailabilityByStationAndDate(stationName, date);
    }

    @Override
    public List<ConnectorUsageResponseDTO> getConnectorUsageByStationAndScope(String stationName, int scope) {
        List<Availability> availabilities = availabilityMapper.getAvailabilityByStationAndScope(stationName, scope);
        Map<String, Map<Integer, List<Availability>>> groupedByConnectorAndHour = availabilities.stream()
                .collect(Collectors.groupingBy(a -> String.valueOf(a.getConnectorId()), Collectors.groupingBy(Availability::getHour)));

        List<ConnectorUsageResponseDTO> response = new ArrayList<>();

        groupedByConnectorAndHour.forEach((connectorId, hourMap) -> {
            List<ConnectorUsageDTO> usageData = new ArrayList<>();
            hourMap.forEach((hour, records) -> {
                long availableCount = records.stream().filter(Availability::getIsAvailable).count();
                double averageUsage = 1 - (double) availableCount / records.size();
                ConnectorUsageDTO usageDTO = new ConnectorUsageDTO();
                usageDTO.setHour(hour);
                usageDTO.setAverageUsage(averageUsage);
                usageData.add(usageDTO);
            });
            ConnectorUsageResponseDTO responseDTO = new ConnectorUsageResponseDTO();
            responseDTO.setConnectorId(connectorId);
            responseDTO.setUsageData(usageData);
            response.add(responseDTO);
        });

        return response;
    }

    @Override
    public List<ConnectorUsageResponseDTO> getConnectorUsageByTimePeriod(String stationName, int scope) {
        List<Availability> availabilities = availabilityMapper.getAvailabilityByStationAndScope(stationName, scope);
        Map<String, Map<Integer, List<Availability>>> groupedByConnectorAndHour = availabilities.stream()
                .collect(Collectors.groupingBy(a -> String.valueOf(a.getConnectorId()), Collectors.groupingBy(Availability::getHour)));

        List<ConnectorUsageResponseDTO> response = new ArrayList<>();

        groupedByConnectorAndHour.forEach((connectorId, hourMap) -> {
            Map<String, List<Availability>> groupedByTimePeriod = groupByTimePeriod(hourMap);
            List<TimePeriodUsageDTO> usageData = new ArrayList<>();
            groupedByTimePeriod.forEach((timePeriod, records) -> {
                long availableCount = records.stream().filter(Availability::getIsAvailable).count();
                double averageUsage = 1 - (double) availableCount / records.size();
                TimePeriodUsageDTO usageDTO = new TimePeriodUsageDTO();
                usageDTO.setTimePeriod(timePeriod);
                usageDTO.setAverageUsage(averageUsage);
                usageData.add(usageDTO);
            });
            ConnectorUsageResponseDTO responseDTO = new ConnectorUsageResponseDTO();
            responseDTO.setConnectorId(connectorId);
            responseDTO.setUsageData(usageData.stream().map(data -> {
                ConnectorUsageDTO connectorUsageDTO = new ConnectorUsageDTO();
                connectorUsageDTO.setHour(Integer.parseInt(data.getTimePeriod().split("-")[0]));
                connectorUsageDTO.setAverageUsage(data.getAverageUsage());
                return connectorUsageDTO;
            }).collect(Collectors.toList()));
            response.add(responseDTO);
        });

        return response;
    }

    private Map<String, List<Availability>> groupByTimePeriod(Map<Integer, List<Availability>> hourMap) {
        Map<String, List<Availability>> groupedByTimePeriod = new HashMap<>();
        for (Map.Entry<Integer, List<Availability>> entry : hourMap.entrySet()) {
            int hour = entry.getKey();
            String timePeriod = getTimePeriod(hour);
            groupedByTimePeriod.computeIfAbsent(timePeriod, k -> new ArrayList<>()).addAll(entry.getValue());
        }
        return groupedByTimePeriod;
    }

    private String getTimePeriod(int hour) {
        if (hour >= 0 && hour <= 6) {
            return "0-6";
        } else if (hour >= 7 && hour <= 10) {
            return "7-10";
        } else if (hour >= 11 && hour <= 16) {
            return "11-16";
        } else if (hour >= 17 && hour <= 20) {
            return "17-20";
        } else {
            return "21-23";
        }
    }


}
