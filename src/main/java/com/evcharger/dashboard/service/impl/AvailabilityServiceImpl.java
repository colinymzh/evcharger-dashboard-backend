package com.evcharger.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evcharger.dashboard.entity.Availability;
import com.evcharger.dashboard.entity.dto.ConnectorUsageDTO;
import com.evcharger.dashboard.entity.dto.ConnectorUsageResponseDTO;
import com.evcharger.dashboard.mapper.AvailabilityMapper;
import com.evcharger.dashboard.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
                double averageUsage = (double) availableCount / records.size();
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
}
