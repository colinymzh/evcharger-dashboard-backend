package com.evcharger.dashboard;

import com.evcharger.dashboard.entity.Availability;
import com.evcharger.dashboard.entity.dto.*;
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

class AvailabilityServiceImplTest {

    @Mock
    private AvailabilityMapper availabilityMapper;

    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetConnectorUsageByTimePeriod_NormalCase() {
        String stationName = "Station1";
        int scope = 7;
        List<Availability> availabilities = Arrays.asList(
                createAvailability(1, 0, true),
                createAvailability(1, 1, false),
                createAvailability(1, 8, true),
                createAvailability(1, 9, false),
                createAvailability(2, 12, true),
                createAvailability(2, 13, false),
                createAvailability(2, 18, true)
        );

        when(availabilityMapper.getAvailabilityByStationAndScope(stationName, scope)).thenReturn(availabilities);

        List<ConnectorUsageResponseDTO> result = availabilityService.getConnectorUsageByTimePeriod(stationName, scope);

        assertEquals(2, result.size());

        ConnectorUsageResponseDTO connector1 = result.get(0);
        assertEquals("1", connector1.getConnectorId());
        assertEquals(2, connector1.getUsageData().size());

        ConnectorUsageResponseDTO connector2 = result.get(1);
        assertEquals("2", connector2.getConnectorId());
        assertEquals(2, connector2.getUsageData().size());

        verify(availabilityMapper, times(1)).getAvailabilityByStationAndScope(stationName, scope);
    }

    @Test
    void testGetConnectorUsageByTimePeriod_EmptyData() {
        String stationName = "EmptyStation";
        int scope = 7;

        when(availabilityMapper.getAvailabilityByStationAndScope(stationName, scope)).thenReturn(Collections.emptyList());

        List<ConnectorUsageResponseDTO> result = availabilityService.getConnectorUsageByTimePeriod(stationName, scope);

        assertTrue(result.isEmpty());
        verify(availabilityMapper, times(1)).getAvailabilityByStationAndScope(stationName, scope);
    }

    @Test
    void testGetConnectorUsageByTimePeriod_SingleConnector() {
        String stationName = "SingleConnectorStation";
        int scope = 7;
        List<Availability> availabilities = Arrays.asList(
                createAvailability(1, 0, true),
                createAvailability(1, 1, false),
                createAvailability(1, 2, true)
        );

        when(availabilityMapper.getAvailabilityByStationAndScope(stationName, scope)).thenReturn(availabilities);

        List<ConnectorUsageResponseDTO> result = availabilityService.getConnectorUsageByTimePeriod(stationName, scope);

        assertEquals(1, result.size());
        ConnectorUsageResponseDTO connector = result.get(0);
        assertEquals("1", connector.getConnectorId());
        assertEquals(1, connector.getUsageData().size());
        assertEquals(0, connector.getUsageData().get(0).getHour());
        assertEquals(1.0/3, connector.getUsageData().get(0).getAverageUsage(), 0.001);

        verify(availabilityMapper, times(1)).getAvailabilityByStationAndScope(stationName, scope);
    }

    @Test
    void testGetConnectorUsageByTimePeriod_AllTimePeriods() {
        String stationName = "AllPeriodsStation";
        int scope = 24;
        List<Availability> availabilities = Arrays.asList(
                createAvailability(1, 2, true),
                createAvailability(1, 8, false),
                createAvailability(1, 13, true),
                createAvailability(1, 18, false),
                createAvailability(1, 22, true)
        );

        when(availabilityMapper.getAvailabilityByStationAndScope(stationName, scope)).thenReturn(availabilities);

        List<ConnectorUsageResponseDTO> result = availabilityService.getConnectorUsageByTimePeriod(stationName, scope);

        assertEquals(1, result.size());
        ConnectorUsageResponseDTO connector = result.get(0);
        assertEquals("1", connector.getConnectorId());
        assertEquals(5, connector.getUsageData().size());

        verify(availabilityMapper, times(1)).getAvailabilityByStationAndScope(stationName, scope);
    }

    @Test
    void testGetConnectorUsageByTimePeriod_FullyAvailable() {
        String stationName = "FullyAvailableStation";
        int scope = 7;
        List<Availability> availabilities = Arrays.asList(
                createAvailability(1, 0, true),
                createAvailability(1, 1, true),
                createAvailability(1, 2, true)
        );

        when(availabilityMapper.getAvailabilityByStationAndScope(stationName, scope)).thenReturn(availabilities);

        List<ConnectorUsageResponseDTO> result = availabilityService.getConnectorUsageByTimePeriod(stationName, scope);

        assertEquals(1, result.size());
        ConnectorUsageResponseDTO connector = result.get(0);
        assertEquals("1", connector.getConnectorId());
        assertEquals(1, connector.getUsageData().size());
        assertEquals(0.0, connector.getUsageData().get(0).getAverageUsage());

        verify(availabilityMapper, times(1)).getAvailabilityByStationAndScope(stationName, scope);
    }

    @Test
    void testGetConnectorUsageByTimePeriod_FullyUnavailable() {
        String stationName = "FullyUnavailableStation";
        int scope = 7;
        List<Availability> availabilities = Arrays.asList(
                createAvailability(1, 0, false),
                createAvailability(1, 1, false),
                createAvailability(1, 2, false)
        );

        when(availabilityMapper.getAvailabilityByStationAndScope(stationName, scope)).thenReturn(availabilities);

        List<ConnectorUsageResponseDTO> result = availabilityService.getConnectorUsageByTimePeriod(stationName, scope);

        assertEquals(1, result.size());
        ConnectorUsageResponseDTO connector = result.get(0);
        assertEquals("1", connector.getConnectorId());
        assertEquals(1, connector.getUsageData().size());
        assertEquals(1.0, connector.getUsageData().get(0).getAverageUsage());

        verify(availabilityMapper, times(1)).getAvailabilityByStationAndScope(stationName, scope);
    }

    @Test
    void testGetConnectorUsageByTimePeriod_MultipleConnectorsMultiplePeriods() {
        String stationName = "MultiConnectorStation";
        int scope = 24;
        List<Availability> availabilities = Arrays.asList(
                createAvailability(1, 2, true),
                createAvailability(1, 8, false),
                createAvailability(2, 13, true),
                createAvailability(2, 18, false),
                createAvailability(3, 22, true)
        );

        when(availabilityMapper.getAvailabilityByStationAndScope(stationName, scope)).thenReturn(availabilities);

        List<ConnectorUsageResponseDTO> result = availabilityService.getConnectorUsageByTimePeriod(stationName, scope);

        assertEquals(3, result.size());
        assertEquals(2, result.get(0).getUsageData().size());
        assertEquals(2, result.get(1).getUsageData().size());
        assertEquals(1, result.get(2).getUsageData().size());

        verify(availabilityMapper, times(1)).getAvailabilityByStationAndScope(stationName, scope);
    }

    @Test
    void testGetConnectorUsageByTimePeriod_BoundaryHours() {
        String stationName = "BoundaryStation";
        int scope = 24;
        List<Availability> availabilities = Arrays.asList(
                createAvailability(1, 0, true),
                createAvailability(1, 6, false),
                createAvailability(1, 7, true),
                createAvailability(1, 10, false),
                createAvailability(1, 11, true),
                createAvailability(1, 16, false),
                createAvailability(1, 17, true),
                createAvailability(1, 20, false),
                createAvailability(1, 21, true),
                createAvailability(1, 23, false)
        );

        when(availabilityMapper.getAvailabilityByStationAndScope(stationName, scope)).thenReturn(availabilities);

        List<ConnectorUsageResponseDTO> result = availabilityService.getConnectorUsageByTimePeriod(stationName, scope);

        assertEquals(1, result.size());
        ConnectorUsageResponseDTO connector = result.get(0);
        assertEquals("1", connector.getConnectorId());
        assertEquals(5, connector.getUsageData().size());

        verify(availabilityMapper, times(1)).getAvailabilityByStationAndScope(stationName, scope);
    }

    @Test
    void testGetConnectorUsageByTimePeriod_LargeDataSet() {
        String stationName = "LargeDataStation";
        int scope = 30;
        List<Availability> availabilities = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            availabilities.add(createAvailability(i % 10 + 1, i % 24, i % 2 == 0));
        }

        when(availabilityMapper.getAvailabilityByStationAndScope(stationName, scope)).thenReturn(availabilities);

        List<ConnectorUsageResponseDTO> result = availabilityService.getConnectorUsageByTimePeriod(stationName, scope);

        assertEquals(10, result.size());
        for (ConnectorUsageResponseDTO connector : result) {
            assertTrue(connector.getUsageData().size() > 0);
        }

        verify(availabilityMapper, times(1)).getAvailabilityByStationAndScope(stationName, scope);
    }

    @Test
    void testGetConnectorUsageByTimePeriod_ExceptionHandling() {
        String stationName = "ExceptionStation";
        int scope = 7;

        when(availabilityMapper.getAvailabilityByStationAndScope(stationName, scope))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () ->
                availabilityService.getConnectorUsageByTimePeriod(stationName, scope)
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
    void testGetWeeklyUsageByStationAndConnector_NormalCase() {
        String stationName = "Station1";
        List<Availability> availabilities = Arrays.asList(
                createAvailability(1, "2023-07-31", true),  // Monday
                createAvailability(1, "2023-08-01", false), // Tuesday
                createAvailability(1, "2023-08-02", true),  // Wednesday
                createAvailability(2, "2023-07-31", false), // Monday
                createAvailability(2, "2023-08-01", true)   // Tuesday
        );

        when(availabilityMapper.getAvailabilityByStationName(stationName)).thenReturn(availabilities);

        List<ConnectorWeeklyUsageResponseDTO> result = availabilityService.getWeeklyUsageByStationAndConnector(stationName);

        assertEquals(2, result.size());

        ConnectorWeeklyUsageResponseDTO connector1 = result.get(0);
        assertEquals("1", connector1.getConnectorId());
        assertEquals(3, connector1.getWeeklyUsage().size());

        ConnectorWeeklyUsageResponseDTO connector2 = result.get(1);
        assertEquals("2", connector2.getConnectorId());
        assertEquals(2, connector2.getWeeklyUsage().size());

        verify(availabilityMapper, times(1)).getAvailabilityByStationName(stationName);
    }

    @Test
    void testGetWeeklyUsageByStationAndConnector_EmptyData() {
        String stationName = "EmptyStation";

        when(availabilityMapper.getAvailabilityByStationName(stationName)).thenReturn(Collections.emptyList());

        List<ConnectorWeeklyUsageResponseDTO> result = availabilityService.getWeeklyUsageByStationAndConnector(stationName);

        assertTrue(result.isEmpty());
        verify(availabilityMapper, times(1)).getAvailabilityByStationName(stationName);
    }

    @Test
    void testGetWeeklyUsageByStationAndConnector_SingleConnectorAllDays() {
        String stationName = "SingleConnectorStation";
        List<Availability> availabilities = Arrays.asList(
                createAvailability(1, "2023-07-31", true),  // Monday
                createAvailability(1, "2023-08-01", false), // Tuesday
                createAvailability(1, "2023-08-02", true),  // Wednesday
                createAvailability(1, "2023-08-03", false), // Thursday
                createAvailability(1, "2023-08-04", true),  // Friday
                createAvailability(1, "2023-08-05", false), // Saturday
                createAvailability(1, "2023-08-06", true)   // Sunday
        );

        when(availabilityMapper.getAvailabilityByStationName(stationName)).thenReturn(availabilities);

        List<ConnectorWeeklyUsageResponseDTO> result = availabilityService.getWeeklyUsageByStationAndConnector(stationName);

        assertEquals(1, result.size());
        ConnectorWeeklyUsageResponseDTO connector = result.get(0);
        assertEquals("1", connector.getConnectorId());
        assertEquals(7, connector.getWeeklyUsage().size());
        assertEquals("MONDAY", connector.getWeeklyUsage().get(0).getDayOfWeek());
        assertEquals("SUNDAY", connector.getWeeklyUsage().get(6).getDayOfWeek());

        verify(availabilityMapper, times(1)).getAvailabilityByStationName(stationName);
    }

    @Test
    void testGetWeeklyUsageByStationAndConnector_MultipleConnectorsSomeDays() {
        String stationName = "MultiConnectorStation";
        List<Availability> availabilities = Arrays.asList(
                createAvailability(1, "2023-07-31", true),  // Monday
                createAvailability(1, "2023-08-02", false), // Wednesday
                createAvailability(2, "2023-08-01", true),  // Tuesday
                createAvailability(2, "2023-08-03", false), // Thursday
                createAvailability(3, "2023-08-04", true)   // Friday
        );

        when(availabilityMapper.getAvailabilityByStationName(stationName)).thenReturn(availabilities);

        List<ConnectorWeeklyUsageResponseDTO> result = availabilityService.getWeeklyUsageByStationAndConnector(stationName);

        assertEquals(3, result.size());
        assertEquals(2, result.get(0).getWeeklyUsage().size());
        assertEquals(2, result.get(1).getWeeklyUsage().size());
        assertEquals(1, result.get(2).getWeeklyUsage().size());

        verify(availabilityMapper, times(1)).getAvailabilityByStationName(stationName);
    }

    @Test
    void testGetWeeklyUsageByStationAndConnector_AverageUsageCalculation() {
        String stationName = "CalculationStation";
        List<Availability> availabilities = Arrays.asList(
                createAvailability(1, "2023-07-31", true),   // Monday
                createAvailability(1, "2023-07-31", false),  // Monday
                createAvailability(1, "2023-07-31", false),  // Monday
                createAvailability(1, "2023-08-01", true),   // Tuesday
                createAvailability(1, "2023-08-01", true)    // Tuesday
        );

        when(availabilityMapper.getAvailabilityByStationName(stationName)).thenReturn(availabilities);

        List<ConnectorWeeklyUsageResponseDTO> result = availabilityService.getWeeklyUsageByStationAndConnector(stationName);

        assertEquals(1, result.size());
        ConnectorWeeklyUsageResponseDTO connector = result.get(0);
        assertEquals("1", connector.getConnectorId());
        assertEquals(2, connector.getWeeklyUsage().size());

        WeeklyUsageDTO monday = connector.getWeeklyUsage().get(0);
        assertEquals("MONDAY", monday.getDayOfWeek());
        assertEquals(2.0/3, monday.getAverageUsage(), 0.001);

        WeeklyUsageDTO tuesday = connector.getWeeklyUsage().get(1);
        assertEquals("TUESDAY", tuesday.getDayOfWeek());
        assertEquals(0.0, tuesday.getAverageUsage(), 0.001);

        verify(availabilityMapper, times(1)).getAvailabilityByStationName(stationName);
    }

    private Availability createAvailability(int connectorId, String date, boolean isAvailable) {
        Availability availability = new Availability();
        availability.setConnectorId(connectorId);
        availability.setDate(date);
        availability.setIsAvailable(isAvailable);
        return availability;
    }

    @Test
    void testGetWeeklyUsageByCity_NormalCase() {
        String stationName = "Station1";
        Integer cityId = 1;
        String cityName = "City1";
        List<Availability> availabilities = Arrays.asList(
                createAvailability("2023-07-31", true),  // Monday
                createAvailability("2023-08-01", false), // Tuesday
                createAvailability("2023-08-02", true),  // Wednesday
                createAvailability("2023-08-03", false), // Thursday
                createAvailability("2023-08-04", true)   // Friday
        );

        when(availabilityMapper.getCityIdByStationName(stationName)).thenReturn(cityId);
        when(availabilityMapper.getAvailabilityByCityId(cityId)).thenReturn(availabilities);
        when(availabilityMapper.getCityNameByCityId(cityId)).thenReturn(cityName);

        CityUsageResponseDTO result = availabilityService.getWeeklyUsageByCity(stationName);

        assertEquals(cityName, result.getCityName());
        assertEquals(5, result.getWeeklyUsage().size());
        assertEquals("MONDAY", result.getWeeklyUsage().get(0).getDayOfWeek());
        assertEquals("FRIDAY", result.getWeeklyUsage().get(4).getDayOfWeek());

        verify(availabilityMapper, times(1)).getCityIdByStationName(stationName);
        verify(availabilityMapper, times(1)).getAvailabilityByCityId(cityId);
        verify(availabilityMapper, times(1)).getCityNameByCityId(cityId);
    }

    @Test
    void testGetWeeklyUsageByCity_EmptyData() {
        String stationName = "EmptyStation";
        Integer cityId = 2;
        String cityName = "EmptyCity";

        when(availabilityMapper.getCityIdByStationName(stationName)).thenReturn(cityId);
        when(availabilityMapper.getAvailabilityByCityId(cityId)).thenReturn(Collections.emptyList());
        when(availabilityMapper.getCityNameByCityId(cityId)).thenReturn(cityName);

        CityUsageResponseDTO result = availabilityService.getWeeklyUsageByCity(stationName);

        assertEquals(cityName, result.getCityName());
        assertTrue(result.getWeeklyUsage().isEmpty());

        verify(availabilityMapper, times(1)).getCityIdByStationName(stationName);
        verify(availabilityMapper, times(1)).getAvailabilityByCityId(cityId);
        verify(availabilityMapper, times(1)).getCityNameByCityId(cityId);
    }

    @Test
    void testGetWeeklyUsageByCity_AllDays() {
        String stationName = "AllDaysStation";
        Integer cityId = 3;
        String cityName = "AllDaysCity";
        List<Availability> availabilities = Arrays.asList(
                createAvailability("2023-07-31", true),  // Monday
                createAvailability("2023-08-01", false), // Tuesday
                createAvailability("2023-08-02", true),  // Wednesday
                createAvailability("2023-08-03", false), // Thursday
                createAvailability("2023-08-04", true),  // Friday
                createAvailability("2023-08-05", false), // Saturday
                createAvailability("2023-08-06", true)   // Sunday
        );

        when(availabilityMapper.getCityIdByStationName(stationName)).thenReturn(cityId);
        when(availabilityMapper.getAvailabilityByCityId(cityId)).thenReturn(availabilities);
        when(availabilityMapper.getCityNameByCityId(cityId)).thenReturn(cityName);

        CityUsageResponseDTO result = availabilityService.getWeeklyUsageByCity(stationName);

        assertEquals(cityName, result.getCityName());
        assertEquals(7, result.getWeeklyUsage().size());
        assertEquals("MONDAY", result.getWeeklyUsage().get(0).getDayOfWeek());
        assertEquals("SUNDAY", result.getWeeklyUsage().get(6).getDayOfWeek());

        verify(availabilityMapper, times(1)).getCityIdByStationName(stationName);
        verify(availabilityMapper, times(1)).getAvailabilityByCityId(cityId);
        verify(availabilityMapper, times(1)).getCityNameByCityId(cityId);
    }

    @Test
    void testGetWeeklyUsageByCity_SomeDays() {
        String stationName = "SomeDaysStation";
        Integer cityId = 4;
        String cityName = "SomeDaysCity";
        List<Availability> availabilities = Arrays.asList(
                createAvailability("2023-07-31", true),  // Monday
                createAvailability("2023-08-02", false), // Wednesday
                createAvailability("2023-08-04", true)   // Friday
        );

        when(availabilityMapper.getCityIdByStationName(stationName)).thenReturn(cityId);
        when(availabilityMapper.getAvailabilityByCityId(cityId)).thenReturn(availabilities);
        when(availabilityMapper.getCityNameByCityId(cityId)).thenReturn(cityName);

        CityUsageResponseDTO result = availabilityService.getWeeklyUsageByCity(stationName);

        assertEquals(cityName, result.getCityName());
        assertEquals(3, result.getWeeklyUsage().size());
        assertEquals("MONDAY", result.getWeeklyUsage().get(0).getDayOfWeek());
        assertEquals("WEDNESDAY", result.getWeeklyUsage().get(1).getDayOfWeek());
        assertEquals("FRIDAY", result.getWeeklyUsage().get(2).getDayOfWeek());

        verify(availabilityMapper, times(1)).getCityIdByStationName(stationName);
        verify(availabilityMapper, times(1)).getAvailabilityByCityId(cityId);
        verify(availabilityMapper, times(1)).getCityNameByCityId(cityId);
    }

    @Test
    void testGetWeeklyUsageByCity_AverageUsageCalculation() {
        String stationName = "CalculationStation";
        Integer cityId = 5;
        String cityName = "CalculationCity";
        List<Availability> availabilities = Arrays.asList(
                createAvailability("2023-07-31", true),   // Monday
                createAvailability("2023-07-31", false),  // Monday
                createAvailability("2023-07-31", false),  // Monday
                createAvailability("2023-08-01", true),   // Tuesday
                createAvailability("2023-08-01", true)    // Tuesday
        );

        when(availabilityMapper.getCityIdByStationName(stationName)).thenReturn(cityId);
        when(availabilityMapper.getAvailabilityByCityId(cityId)).thenReturn(availabilities);
        when(availabilityMapper.getCityNameByCityId(cityId)).thenReturn(cityName);

        CityUsageResponseDTO result = availabilityService.getWeeklyUsageByCity(stationName);

        assertEquals(cityName, result.getCityName());
        assertEquals(2, result.getWeeklyUsage().size());

        WeeklyCityUsageDTO monday = result.getWeeklyUsage().get(0);
        assertEquals("MONDAY", monday.getDayOfWeek());
        assertEquals(2.0/3, monday.getAverageUsage(), 0.001);

        WeeklyCityUsageDTO tuesday = result.getWeeklyUsage().get(1);
        assertEquals("TUESDAY", tuesday.getDayOfWeek());
        assertEquals(0.0, tuesday.getAverageUsage(), 0.001);

        verify(availabilityMapper, times(1)).getCityIdByStationName(stationName);
        verify(availabilityMapper, times(1)).getAvailabilityByCityId(cityId);
        verify(availabilityMapper, times(1)).getCityNameByCityId(cityId);
    }

    private Availability createAvailability(String date, boolean isAvailable) {
        Availability availability = new Availability();
        availability.setDate(date);
        availability.setIsAvailable(isAvailable);
        return availability;
    }
}