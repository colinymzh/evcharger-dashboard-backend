package com.evcharger.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evcharger.dashboard.entity.Availability;
import com.evcharger.dashboard.entity.dto.*;
import com.evcharger.dashboard.mapper.AvailabilityMapper;
import com.evcharger.dashboard.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AvailabilityServiceImpl extends ServiceImpl<AvailabilityMapper, Availability> implements AvailabilityService {

    @Autowired
    private AvailabilityMapper availabilityMapper;
    @Override
    public List<Availability> getAvailabilityByStationAndDate(String stationName, String date) {
        return baseMapper.getAvailabilityByStationAndDate(stationName, date);
    }

    //时间点、可用性、折线图
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

    //时间段、可用性、折线图
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


    @Override
    public List<ConnectorWeeklyUsageResponseDTO> getWeeklyUsageByStationAndConnector(String stationName) {
        List<Availability> availabilities = availabilityMapper.getAvailabilityByStationName(stationName);

        Map<String, Map<DayOfWeek, List<Availability>>> groupedByConnectorAndDayOfWeek = availabilities.stream()
                .collect(Collectors.groupingBy(a -> String.valueOf(a.getConnectorId()),
                        Collectors.groupingBy(a -> LocalDate.parse(a.getDate()).getDayOfWeek())));

        List<ConnectorWeeklyUsageResponseDTO> response = new ArrayList<>();

        groupedByConnectorAndDayOfWeek.forEach((connectorId, dayOfWeekMap) -> {
            List<WeeklyUsageDTO> weeklyUsage = new ArrayList<>();
            dayOfWeekMap.forEach((dayOfWeek, records) -> {
                long totalCount = records.size();
                long unavailableCount = records.stream().filter(a -> !a.getIsAvailable()).count();
                double averageUsage = (double) unavailableCount / totalCount;
                WeeklyUsageDTO usageDTO = new WeeklyUsageDTO();
                usageDTO.setDayOfWeek(dayOfWeek.toString());
                usageDTO.setAverageUsage(averageUsage);
                weeklyUsage.add(usageDTO);
            });
            ConnectorWeeklyUsageResponseDTO responseDTO = new ConnectorWeeklyUsageResponseDTO();
            responseDTO.setConnectorId(connectorId);
            responseDTO.setWeeklyUsage(weeklyUsage.stream()
                    .sorted(Comparator.comparingInt(this::getDayOfWeekOrder))
                    .collect(Collectors.toList()));
            response.add(responseDTO);
        });

        return response;
    }

    private int getDayOfWeekOrder(WeeklyUsageDTO dto) {
        switch (dto.getDayOfWeek()) {
            case "MONDAY": return 1;
            case "TUESDAY": return 2;
            case "WEDNESDAY": return 3;
            case "THURSDAY": return 4;
            case "FRIDAY": return 5;
            case "SATURDAY": return 6;
            case "SUNDAY": return 7;
            default: return 0;
        }
    }

    //星期、城市、柱状图
    @Override
    public CityUsageResponseDTO getWeeklyUsageByCity(String stationName) {
        Integer cityId = availabilityMapper.getCityIdByStationName(stationName);
        List<Availability> availabilities = availabilityMapper.getAvailabilityByCityId(cityId);
        String cityName = availabilityMapper.getCityNameByCityId(cityId);

        Map<DayOfWeek, List<Availability>> groupedByDayOfWeek = availabilities.stream()
                .collect(Collectors.groupingBy(a -> LocalDate.parse(a.getDate()).getDayOfWeek()));

        List<WeeklyCityUsageDTO> weeklyUsage = new ArrayList<>();

        groupedByDayOfWeek.forEach((dayOfWeek, records) -> {
            long totalCount = records.size();
            long unavailableCount = records.stream().filter(a -> !a.getIsAvailable()).count();
            double averageUsage = (double) unavailableCount / totalCount;
            WeeklyCityUsageDTO usageDTO = new WeeklyCityUsageDTO();
            usageDTO.setDayOfWeek(dayOfWeek.toString());
            usageDTO.setAverageUsage(averageUsage);
            weeklyUsage.add(usageDTO);
        });

        CityUsageResponseDTO responseDTO = new CityUsageResponseDTO();
        responseDTO.setCityName(cityName);
        responseDTO.setWeeklyUsage(weeklyUsage.stream()
                .sorted(Comparator.comparingInt(this::getDayOfWeekOrder))
                .collect(Collectors.toList()));

        return responseDTO;
    }

    private int getDayOfWeekOrder(WeeklyCityUsageDTO dto) {
        switch (dto.getDayOfWeek()) {
            case "MONDAY": return 1;
            case "TUESDAY": return 2;
            case "WEDNESDAY": return 3;
            case "THURSDAY": return 4;
            case "FRIDAY": return 5;
            case "SATURDAY": return 6;
            case "SUNDAY": return 7;
            default: return 0;
        }
    }


    //时间点、星期数、热度图
    @Override
    public List<ConnectorWeeklyHourlyUsageDTO> getWeeklyHourlyUsageByStationAndConnector(String stationName) {
        List<Availability> availabilities = availabilityMapper.getAvailabilityByStationName(stationName);

        Map<String, Map<DayOfWeek, Map<Integer, List<Availability>>>> groupedByConnectorAndDayOfWeekAndHour = availabilities.stream()
                .collect(Collectors.groupingBy(
                        a -> String.valueOf(a.getConnectorId()),
                        Collectors.groupingBy(
                                a -> LocalDate.parse(a.getDate()).getDayOfWeek(),
                                Collectors.groupingBy(Availability::getHour)
                        )
                ));

        List<ConnectorWeeklyHourlyUsageDTO> response = new ArrayList<>();

        groupedByConnectorAndDayOfWeekAndHour.forEach((connectorId, dayOfWeekMap) -> {
            List<WeeklyHourlyUsageDTO> weeklyHourlyUsageList = new ArrayList<>();
            dayOfWeekMap.forEach((dayOfWeek, hourMap) -> {
                List<HourlyUsageDTO> hourlyUsageList = new ArrayList<>();
                hourMap.forEach((hour, records) -> {
                    long totalCount = records.size();
                    long unavailableCount = records.stream().filter(a -> !a.getIsAvailable()).count();
                    double averageUsage = (double) unavailableCount / totalCount;
                    HourlyUsageDTO hourlyUsageDTO = new HourlyUsageDTO();
                    hourlyUsageDTO.setHour(hour);
                    hourlyUsageDTO.setAverageUsage(averageUsage);
                    hourlyUsageList.add(hourlyUsageDTO);
                });
                WeeklyHourlyUsageDTO weeklyHourlyUsageDTO = new WeeklyHourlyUsageDTO();
                weeklyHourlyUsageDTO.setDayOfWeek(dayOfWeek.toString());
                weeklyHourlyUsageDTO.setHourlyUsage(hourlyUsageList.stream()
                        .sorted(Comparator.comparingInt(HourlyUsageDTO::getHour))
                        .collect(Collectors.toList()));
                weeklyHourlyUsageList.add(weeklyHourlyUsageDTO);
            });
            ConnectorWeeklyHourlyUsageDTO responseDTO = new ConnectorWeeklyHourlyUsageDTO();
            responseDTO.setConnectorId(connectorId);
            responseDTO.setWeeklyHourlyUsage(weeklyHourlyUsageList.stream()
                    .sorted(Comparator.comparingInt(this::getDayOfWeekOrder))
                    .collect(Collectors.toList()));
            response.add(responseDTO);
        });

        return response;
    }

    private int getDayOfWeekOrder(WeeklyHourlyUsageDTO dto) {
        switch (dto.getDayOfWeek()) {
            case "MONDAY": return 1;
            case "TUESDAY": return 2;
            case "WEDNESDAY": return 3;
            case "THURSDAY": return 4;
            case "FRIDAY": return 5;
            case "SATURDAY": return 6;
            case "SUNDAY": return 7;
            default: return 0;
        }
    }



}
