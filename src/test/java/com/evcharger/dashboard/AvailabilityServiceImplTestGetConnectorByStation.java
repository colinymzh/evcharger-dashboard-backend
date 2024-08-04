package com.evcharger.dashboard;

import com.evcharger.dashboard.entity.Availability;
import com.evcharger.dashboard.entity.dto.ConnectorUsageDTO;
import com.evcharger.dashboard.entity.dto.ConnectorUsageResponseDTO;
import com.evcharger.dashboard.entity.dto.ConnectorWeeklyHourlyUsageDTO;
import com.evcharger.dashboard.entity.dto.HourlyUsageDTO;
import com.evcharger.dashboard.mapper.AvailabilityMapper;
import com.evcharger.dashboard.service.impl.AvailabilityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AvailabilityServiceImplTestGetConnectorByStation {

    @Mock
    private AvailabilityMapper availabilityMapper;

    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetConnectorUsageByStationAndScope_NormalCase() {
        // 准备测试数据
        String stationName = "Station1";
        int scope = 7;
        List<Availability> availabilities = Arrays.asList(
                createAvailability(1, 0, true),
                createAvailability(1, 0, false),
                createAvailability(1, 1, true),
                createAvailability(1, 1, true),
                createAvailability(2, 0, false),
                createAvailability(2, 0, true),
                createAvailability(2, 1, false)
        );

        // 配置mock行为
        when(availabilityMapper.getAvailabilityByStationAndScope(stationName, scope)).thenReturn(availabilities);

        // 执行方法
        List<ConnectorUsageResponseDTO> result = availabilityService.getConnectorUsageByStationAndScope(stationName, scope);

        // 验证结果
        assertEquals(2, result.size());

        ConnectorUsageResponseDTO connector1 = result.get(0);
        assertEquals("1", connector1.getConnectorId());
        assertEquals(2, connector1.getUsageData().size());
        assertEquals(0.5, connector1.getUsageData().get(0).getAverageUsage());
        assertEquals(0.0, connector1.getUsageData().get(1).getAverageUsage());

        ConnectorUsageResponseDTO connector2 = result.get(1);
        assertEquals("2", connector2.getConnectorId());
        assertEquals(2, connector2.getUsageData().size());
        assertEquals(0.5, connector2.getUsageData().get(0).getAverageUsage());
        assertEquals(1.0, connector2.getUsageData().get(1).getAverageUsage());

        // 验证mock方法被调用
        verify(availabilityMapper, times(1)).getAvailabilityByStationAndScope(stationName, scope);
    }

    @Test
    void testGetConnectorUsageByStationAndScope_EmptyData() {
        String stationName = "EmptyStation";
        int scope = 7;

        when(availabilityMapper.getAvailabilityByStationAndScope(stationName, scope)).thenReturn(Arrays.asList());

        List<ConnectorUsageResponseDTO> result = availabilityService.getConnectorUsageByStationAndScope(stationName, scope);

        assertTrue(result.isEmpty());
        verify(availabilityMapper, times(1)).getAvailabilityByStationAndScope(stationName, scope);
    }

    @Test
    void testGetConnectorUsageByStationAndScope_SingleConnector() {
        String stationName = "SingleConnectorStation";
        int scope = 7;
        List<Availability> availabilities = Arrays.asList(
                createAvailability(1, 0, true),
                createAvailability(1, 0, false),
                createAvailability(1, 1, true)
        );

        when(availabilityMapper.getAvailabilityByStationAndScope(stationName, scope)).thenReturn(availabilities);

        List<ConnectorUsageResponseDTO> result = availabilityService.getConnectorUsageByStationAndScope(stationName, scope);

        assertEquals(1, result.size());
        ConnectorUsageResponseDTO connector = result.get(0);
        assertEquals("1", connector.getConnectorId());
        assertEquals(2, connector.getUsageData().size());
        assertEquals(0.5, connector.getUsageData().get(0).getAverageUsage());
        assertEquals(0.0, connector.getUsageData().get(1).getAverageUsage());

        verify(availabilityMapper, times(1)).getAvailabilityByStationAndScope(stationName, scope);
    }

    @Test
    void testGetConnectorUsageByStationAndScope_LargeScope() {
        String stationName = "LargeScopeStation";
        int scope = 365; // 一年的数据

        List<Availability> availabilities = Arrays.asList(
                createAvailability(1, 0, true),
                createAvailability(1, 23, false)
        );

        when(availabilityMapper.getAvailabilityByStationAndScope(stationName, scope)).thenReturn(availabilities);

        List<ConnectorUsageResponseDTO> result = availabilityService.getConnectorUsageByStationAndScope(stationName, scope);

        assertEquals(1, result.size());
        ConnectorUsageResponseDTO connector = result.get(0);
        assertEquals("1", connector.getConnectorId());
        assertEquals(2, connector.getUsageData().size());

        // 查找特定小时的数据
        ConnectorUsageDTO hour0Data = findUsageDataForHour(connector.getUsageData(), 0);
        ConnectorUsageDTO hour23Data = findUsageDataForHour(connector.getUsageData(), 23);

        assertNotNull(hour0Data);
        assertNotNull(hour23Data);
        assertEquals(0.0, hour0Data.getAverageUsage());
        assertEquals(1.0, hour23Data.getAverageUsage());

        verify(availabilityMapper, times(1)).getAvailabilityByStationAndScope(stationName, scope);
    }

    private ConnectorUsageDTO findUsageDataForHour(List<ConnectorUsageDTO> usageData, int hour) {
        return usageData.stream()
                .filter(data -> data.getHour() == hour)
                .findFirst()
                .orElse(null);
    }

    @Test
    void testGetConnectorUsageByStationAndScope_ExceptionHandling() {
        String stationName = "ExceptionStation";
        int scope = 7;

        when(availabilityMapper.getAvailabilityByStationAndScope(stationName, scope))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () ->
                availabilityService.getConnectorUsageByStationAndScope(stationName, scope)
        );

        verify(availabilityMapper, times(1)).getAvailabilityByStationAndScope(stationName, scope);
    }

    private Availability createAvailability(int connectorId, int hour, boolean isAvailable) {
        Availability availability = new Availability();
        availability.setConnectorId(connectorId);
        availability.setHour(hour);
        availability.setIsAvailable(isAvailable);
        return availability;
    }
    @Test
    void testGetWeeklyHourlyUsageByStationAndConnector_NormalCase() {
        String stationName = "Station1";
        List<Availability> availabilities = Arrays.asList(
                createAvailability("2023-07-31", 1, 0, true),   // Monday
                createAvailability("2023-07-31", 1, 1, false),  // Monday
                createAvailability("2023-08-01", 1, 0, true),   // Tuesday
                createAvailability("2023-08-01", 1, 1, false),  // Tuesday
                createAvailability("2023-08-01", 2, 0, true),   // Tuesday, different connector
                createAvailability("2023-08-01", 2, 1, true)    // Tuesday, different connector
        );

        when(availabilityMapper.getAvailabilityByStationName(stationName)).thenReturn(availabilities);

        List<ConnectorWeeklyHourlyUsageDTO> result = availabilityService.getWeeklyHourlyUsageByStationAndConnector(stationName);

        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getConnectorId());
        assertEquals("2", result.get(1).getConnectorId());

        assertEquals(2, result.get(0).getWeeklyHourlyUsage().size());
        assertEquals("MONDAY", result.get(0).getWeeklyHourlyUsage().get(0).getDayOfWeek());
        assertEquals("TUESDAY", result.get(0).getWeeklyHourlyUsage().get(1).getDayOfWeek());

        verify(availabilityMapper, times(1)).getAvailabilityByStationName(stationName);
    }

    @Test
    void testGetWeeklyHourlyUsageByStationAndConnector_EmptyData() {
        String stationName = "EmptyStation";

        when(availabilityMapper.getAvailabilityByStationName(stationName)).thenReturn(Collections.emptyList());

        List<ConnectorWeeklyHourlyUsageDTO> result = availabilityService.getWeeklyHourlyUsageByStationAndConnector(stationName);

        assertTrue(result.isEmpty());

        verify(availabilityMapper, times(1)).getAvailabilityByStationName(stationName);
    }

    @Test
    void testGetWeeklyHourlyUsageByStationAndConnector_AllDaysAndHours() {
        String stationName = "AllDaysAndHoursStation";
        List<Availability> availabilities = new ArrayList<>();
        for (int day = 1; day <= 7; day++) {
            for (int hour = 0; hour < 24; hour++) {
                availabilities.add(createAvailability("2023-07-3" + day, 1, hour, true));
                availabilities.add(createAvailability("2023-07-3" + day, 1, hour, false));
            }
        }

        when(availabilityMapper.getAvailabilityByStationName(stationName)).thenReturn(availabilities);

        List<ConnectorWeeklyHourlyUsageDTO> result = availabilityService.getWeeklyHourlyUsageByStationAndConnector(stationName);

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getConnectorId());
        assertEquals(7, result.get(0).getWeeklyHourlyUsage().size());
        assertEquals(24, result.get(0).getWeeklyHourlyUsage().get(0).getHourlyUsage().size());

        verify(availabilityMapper, times(1)).getAvailabilityByStationName(stationName);
    }

    @Test
    void testGetWeeklyHourlyUsageByStationAndConnector_MultipleConnectors() {
        String stationName = "MultiConnectorStation";
        List<Availability> availabilities = Arrays.asList(
                createAvailability("2023-07-31", 1, 0, true),
                createAvailability("2023-07-31", 2, 0, false),
                createAvailability("2023-07-31", 3, 0, true)
        );

        when(availabilityMapper.getAvailabilityByStationName(stationName)).thenReturn(availabilities);

        List<ConnectorWeeklyHourlyUsageDTO> result = availabilityService.getWeeklyHourlyUsageByStationAndConnector(stationName);

        assertEquals(3, result.size());
        assertEquals("1", result.get(0).getConnectorId());
        assertEquals("2", result.get(1).getConnectorId());
        assertEquals("3", result.get(2).getConnectorId());

        verify(availabilityMapper, times(1)).getAvailabilityByStationName(stationName);
    }

    @Test
    void testGetWeeklyHourlyUsageByStationAndConnector_AverageUsageCalculation() {
        String stationName = "CalculationStation";
        List<Availability> availabilities = Arrays.asList(
                createAvailability("2023-07-31", 1, 0, true),
                createAvailability("2023-07-31", 1, 0, false),
                createAvailability("2023-07-31", 1, 0, false),
                createAvailability("2023-07-31", 1, 1, true),
                createAvailability("2023-07-31", 1, 1, true)
        );

        when(availabilityMapper.getAvailabilityByStationName(stationName)).thenReturn(availabilities);

        List<ConnectorWeeklyHourlyUsageDTO> result = availabilityService.getWeeklyHourlyUsageByStationAndConnector(stationName);

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getConnectorId());
        assertEquals(1, result.get(0).getWeeklyHourlyUsage().size());
        assertEquals("MONDAY", result.get(0).getWeeklyHourlyUsage().get(0).getDayOfWeek());

        List<HourlyUsageDTO> hourlyUsage = result.get(0).getWeeklyHourlyUsage().get(0).getHourlyUsage();
        assertEquals(2, hourlyUsage.size());
        assertEquals(0, hourlyUsage.get(0).getHour());
        assertEquals(2.0/3, hourlyUsage.get(0).getAverageUsage(), 0.001);
        assertEquals(1, hourlyUsage.get(1).getHour());
        assertEquals(0.0, hourlyUsage.get(1).getAverageUsage(), 0.001);

        verify(availabilityMapper, times(1)).getAvailabilityByStationName(stationName);
    }

    private Availability createAvailability(String date, int connectorId, int hour, boolean isAvailable) {
        Availability availability = new Availability();
        availability.setDate(date);
        availability.setConnectorId(connectorId);
        availability.setHour(hour);
        availability.setIsAvailable(isAvailable);
        return availability;
    }


}