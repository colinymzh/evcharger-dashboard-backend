package com.evcharger.dashboard;

import com.evcharger.dashboard.controller.AvailabilityController;
import com.evcharger.dashboard.entity.Availability;
import com.evcharger.dashboard.entity.dto.*;
import com.evcharger.dashboard.service.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

public class AvailabilityControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AvailabilityService availabilityService;

    @InjectMocks
    private AvailabilityController availabilityController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(availabilityController).build();
    }

    @Test
    public void testGetAvailabilityByStationAndDate_Success() throws Exception {
        String stationName = "Station1";
        String date = "2023-08-04";

        Availability availability = new Availability();
        availability.setStationName(stationName);
        availability.setDate(date);
        availability.setConnectorId(1);
        availability.setHour(12);
        availability.setIsAvailable(true);
        availability.setCityId("City1");

        List<Availability> availabilityList = Collections.singletonList(availability);

        when(availabilityService.getAvailabilityByStationAndDate(stationName, date)).thenReturn(availabilityList);

        mockMvc.perform(get("/availability/station")
                        .param("stationName", stationName)
                        .param("date", date)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].stationName", is(stationName)))
                .andExpect(jsonPath("$[0].date", is(date)))
                .andExpect(jsonPath("$[0].connectorId", is(1)))
                .andExpect(jsonPath("$[0].hour", is(12)))
                .andExpect(jsonPath("$[0].isAvailable", is(true)))
                .andExpect(jsonPath("$[0].cityId", is("City1")));

        verify(availabilityService, times(1)).getAvailabilityByStationAndDate(stationName, date);
    }

    @Test
    public void testGetAvailabilityByStationAndDate_EmptyResult() throws Exception {
        String stationName = "NonExistentStation";
        String date = "2023-08-04";

        when(availabilityService.getAvailabilityByStationAndDate(stationName, date)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/availability/station")
                        .param("stationName", stationName)
                        .param("date", date)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(availabilityService, times(1)).getAvailabilityByStationAndDate(stationName, date);
    }

    @Test
    public void testGetAvailabilityByStationAndDate_MultipleResults() throws Exception {
        String stationName = "Station1";
        String date = "2023-08-04";

        Availability availability1 = new Availability();
        availability1.setStationName(stationName);
        availability1.setDate(date);
        availability1.setConnectorId(1);
        availability1.setHour(12);
        availability1.setIsAvailable(true);
        availability1.setCityId("City1");

        Availability availability2 = new Availability();
        availability2.setStationName(stationName);
        availability2.setDate(date);
        availability2.setConnectorId(2);
        availability2.setHour(13);
        availability2.setIsAvailable(false);
        availability2.setCityId("City1");

        List<Availability> availabilityList = Arrays.asList(availability1, availability2);

        when(availabilityService.getAvailabilityByStationAndDate(stationName, date)).thenReturn(availabilityList);

        mockMvc.perform(get("/availability/station")
                        .param("stationName", stationName)
                        .param("date", date)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].stationName", is(stationName)))
                .andExpect(jsonPath("$[0].date", is(date)))
                .andExpect(jsonPath("$[0].connectorId", is(1)))
                .andExpect(jsonPath("$[0].hour", is(12)))
                .andExpect(jsonPath("$[0].isAvailable", is(true)))
                .andExpect(jsonPath("$[0].cityId", is("City1")))
                .andExpect(jsonPath("$[1].stationName", is(stationName)))
                .andExpect(jsonPath("$[1].date", is(date)))
                .andExpect(jsonPath("$[1].connectorId", is(2)))
                .andExpect(jsonPath("$[1].hour", is(13)))
                .andExpect(jsonPath("$[1].isAvailable", is(false)))
                .andExpect(jsonPath("$[1].cityId", is("City1")));

        verify(availabilityService, times(1)).getAvailabilityByStationAndDate(stationName, date);
    }

    @Test
    public void testGetConnectorUsageByStationAndScope_Success() throws Exception {
        String stationName = "Station1";
        int scope = 7;

        ConnectorUsageDTO usageDTO = new ConnectorUsageDTO();
        usageDTO.setHour(12);
        usageDTO.setAverageUsage(0.75);

        ConnectorUsageResponseDTO responseDTO = new ConnectorUsageResponseDTO();
        responseDTO.setConnectorId("1");
        responseDTO.setUsageData(Collections.singletonList(usageDTO));

        List<ConnectorUsageResponseDTO> responseDTOList = Collections.singletonList(responseDTO);

        when(availabilityService.getConnectorUsageByStationAndScope(stationName, scope)).thenReturn(responseDTOList);

        mockMvc.perform(get("/availability/station/usage")
                        .param("stationName", stationName)
                        .param("scope", String.valueOf(scope))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].connectorId", is("1")))
                .andExpect(jsonPath("$[0].usageData", hasSize(1)))
                .andExpect(jsonPath("$[0].usageData[0].hour", is(12)))
                .andExpect(jsonPath("$[0].usageData[0].averageUsage", is(0.75)));

        verify(availabilityService, times(1)).getConnectorUsageByStationAndScope(stationName, scope);
    }

    @Test
    public void testGetConnectorUsageByStationAndScope_EmptyResult() throws Exception {
        String stationName = "NonExistentStation";
        int scope = 7;

        when(availabilityService.getConnectorUsageByStationAndScope(stationName, scope)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/availability/station/usage")
                        .param("stationName", stationName)
                        .param("scope", String.valueOf(scope))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(availabilityService, times(1)).getConnectorUsageByStationAndScope(stationName, scope);
    }

    @Test
    public void testGetConnectorUsageByStationAndScope_MultipleConnectors() throws Exception {
        String stationName = "Station1";
        int scope = 7;

        ConnectorUsageDTO usageDTO1 = new ConnectorUsageDTO();
        usageDTO1.setHour(12);
        usageDTO1.setAverageUsage(0.75);

        ConnectorUsageDTO usageDTO2 = new ConnectorUsageDTO();
        usageDTO2.setHour(13);
        usageDTO2.setAverageUsage(0.80);

        ConnectorUsageResponseDTO responseDTO1 = new ConnectorUsageResponseDTO();
        responseDTO1.setConnectorId("1");
        responseDTO1.setUsageData(Collections.singletonList(usageDTO1));

        ConnectorUsageResponseDTO responseDTO2 = new ConnectorUsageResponseDTO();
        responseDTO2.setConnectorId("2");
        responseDTO2.setUsageData(Collections.singletonList(usageDTO2));

        List<ConnectorUsageResponseDTO> responseDTOList = Arrays.asList(responseDTO1, responseDTO2);

        when(availabilityService.getConnectorUsageByStationAndScope(stationName, scope)).thenReturn(responseDTOList);

        mockMvc.perform(get("/availability/station/usage")
                        .param("stationName", stationName)
                        .param("scope", String.valueOf(scope))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].connectorId", is("1")))
                .andExpect(jsonPath("$[0].usageData[0].hour", is(12)))
                .andExpect(jsonPath("$[0].usageData[0].averageUsage", is(0.75)))
                .andExpect(jsonPath("$[1].connectorId", is("2")))
                .andExpect(jsonPath("$[1].usageData[0].hour", is(13)))
                .andExpect(jsonPath("$[1].usageData[0].averageUsage", is(0.80)));

        verify(availabilityService, times(1)).getConnectorUsageByStationAndScope(stationName, scope);
    }

    @Test
    public void testGetConnectorUsageByTimePeriod_Success() throws Exception {
        String stationName = "Station1";
        int scope = 7;

        ConnectorUsageDTO usageDTO = new ConnectorUsageDTO();
        usageDTO.setHour(12);
        usageDTO.setAverageUsage(0.75);

        ConnectorUsageResponseDTO responseDTO = new ConnectorUsageResponseDTO();
        responseDTO.setConnectorId("1");
        responseDTO.setUsageData(Collections.singletonList(usageDTO));

        List<ConnectorUsageResponseDTO> responseDTOList = Collections.singletonList(responseDTO);

        when(availabilityService.getConnectorUsageByTimePeriod(stationName, scope)).thenReturn(responseDTOList);

        mockMvc.perform(get("/availability/station/usage/time-period")
                        .param("stationName", stationName)
                        .param("scope", String.valueOf(scope))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].connectorId", is("1")))
                .andExpect(jsonPath("$[0].usageData", hasSize(1)))
                .andExpect(jsonPath("$[0].usageData[0].hour", is(12)))
                .andExpect(jsonPath("$[0].usageData[0].averageUsage", is(0.75)));

        verify(availabilityService, times(1)).getConnectorUsageByTimePeriod(stationName, scope);
    }

    @Test
    public void testGetConnectorUsageByTimePeriod_EmptyResult() throws Exception {
        String stationName = "NonExistentStation";
        int scope = 7;

        when(availabilityService.getConnectorUsageByTimePeriod(stationName, scope)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/availability/station/usage/time-period")
                        .param("stationName", stationName)
                        .param("scope", String.valueOf(scope))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(availabilityService, times(1)).getConnectorUsageByTimePeriod(stationName, scope);
    }

    @Test
    public void testGetConnectorUsageByTimePeriod_MultipleConnectorsAndPeriods() throws Exception {
        String stationName = "Station1";
        int scope = 7;

        ConnectorUsageDTO usageDTO1 = new ConnectorUsageDTO();
        usageDTO1.setHour(12);
        usageDTO1.setAverageUsage(0.75);

        ConnectorUsageDTO usageDTO2 = new ConnectorUsageDTO();
        usageDTO2.setHour(18);
        usageDTO2.setAverageUsage(0.80);

        ConnectorUsageResponseDTO responseDTO1 = new ConnectorUsageResponseDTO();
        responseDTO1.setConnectorId("1");
        responseDTO1.setUsageData(Arrays.asList(usageDTO1, usageDTO2));

        ConnectorUsageDTO usageDTO3 = new ConnectorUsageDTO();
        usageDTO3.setHour(12);
        usageDTO3.setAverageUsage(0.70);

        ConnectorUsageDTO usageDTO4 = new ConnectorUsageDTO();
        usageDTO4.setHour(18);
        usageDTO4.setAverageUsage(0.85);

        ConnectorUsageResponseDTO responseDTO2 = new ConnectorUsageResponseDTO();
        responseDTO2.setConnectorId("2");
        responseDTO2.setUsageData(Arrays.asList(usageDTO3, usageDTO4));

        List<ConnectorUsageResponseDTO> responseDTOList = Arrays.asList(responseDTO1, responseDTO2);

        when(availabilityService.getConnectorUsageByTimePeriod(stationName, scope)).thenReturn(responseDTOList);

        mockMvc.perform(get("/availability/station/usage/time-period")
                        .param("stationName", stationName)
                        .param("scope", String.valueOf(scope))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].connectorId", is("1")))
                .andExpect(jsonPath("$[0].usageData", hasSize(2)))
                .andExpect(jsonPath("$[0].usageData[0].hour", is(12)))
                .andExpect(jsonPath("$[0].usageData[0].averageUsage", is(0.75)))
                .andExpect(jsonPath("$[0].usageData[1].hour", is(18)))
                .andExpect(jsonPath("$[0].usageData[1].averageUsage", is(0.80)))
                .andExpect(jsonPath("$[1].connectorId", is("2")))
                .andExpect(jsonPath("$[1].usageData", hasSize(2)))
                .andExpect(jsonPath("$[1].usageData[0].hour", is(12)))
                .andExpect(jsonPath("$[1].usageData[0].averageUsage", is(0.70)))
                .andExpect(jsonPath("$[1].usageData[1].hour", is(18)))
                .andExpect(jsonPath("$[1].usageData[1].averageUsage", is(0.85)));

        verify(availabilityService, times(1)).getConnectorUsageByTimePeriod(stationName, scope);
    }

    @Test
    public void testGetWeeklyUsageByStationAndConnector_Success() throws Exception {
        String stationName = "Station1";

        WeeklyUsageDTO usageDTO = new WeeklyUsageDTO();
        usageDTO.setDayOfWeek("MONDAY");
        usageDTO.setAverageUsage(0.75);

        ConnectorWeeklyUsageResponseDTO responseDTO = new ConnectorWeeklyUsageResponseDTO();
        responseDTO.setConnectorId("1");
        responseDTO.setWeeklyUsage(Collections.singletonList(usageDTO));

        List<ConnectorWeeklyUsageResponseDTO> responseDTOList = Collections.singletonList(responseDTO);

        when(availabilityService.getWeeklyUsageByStationAndConnector(stationName)).thenReturn(responseDTOList);

        mockMvc.perform(get("/availability/station/weekly-usage")
                        .param("stationName", stationName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].connectorId", is("1")))
                .andExpect(jsonPath("$[0].weeklyUsage", hasSize(1)))
                .andExpect(jsonPath("$[0].weeklyUsage[0].dayOfWeek", is("MONDAY")))
                .andExpect(jsonPath("$[0].weeklyUsage[0].averageUsage", is(0.75)));

        verify(availabilityService, times(1)).getWeeklyUsageByStationAndConnector(stationName);
    }

    @Test
    public void testGetWeeklyUsageByStationAndConnector_EmptyResult() throws Exception {
        String stationName = "NonExistentStation";

        when(availabilityService.getWeeklyUsageByStationAndConnector(stationName)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/availability/station/weekly-usage")
                        .param("stationName", stationName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(availabilityService, times(1)).getWeeklyUsageByStationAndConnector(stationName);
    }

    @Test
    public void testGetWeeklyUsageByStationAndConnector_MultipleConnectorsAndDays() throws Exception {
        String stationName = "Station1";

        WeeklyUsageDTO usageDTO1 = new WeeklyUsageDTO();
        usageDTO1.setDayOfWeek("MONDAY");
        usageDTO1.setAverageUsage(0.75);

        WeeklyUsageDTO usageDTO2 = new WeeklyUsageDTO();
        usageDTO2.setDayOfWeek("TUESDAY");
        usageDTO2.setAverageUsage(0.80);

        ConnectorWeeklyUsageResponseDTO responseDTO1 = new ConnectorWeeklyUsageResponseDTO();
        responseDTO1.setConnectorId("1");
        responseDTO1.setWeeklyUsage(Arrays.asList(usageDTO1, usageDTO2));

        WeeklyUsageDTO usageDTO3 = new WeeklyUsageDTO();
        usageDTO3.setDayOfWeek("MONDAY");
        usageDTO3.setAverageUsage(0.70);

        WeeklyUsageDTO usageDTO4 = new WeeklyUsageDTO();
        usageDTO4.setDayOfWeek("TUESDAY");
        usageDTO4.setAverageUsage(0.85);

        ConnectorWeeklyUsageResponseDTO responseDTO2 = new ConnectorWeeklyUsageResponseDTO();
        responseDTO2.setConnectorId("2");
        responseDTO2.setWeeklyUsage(Arrays.asList(usageDTO3, usageDTO4));

        List<ConnectorWeeklyUsageResponseDTO> responseDTOList = Arrays.asList(responseDTO1, responseDTO2);

        when(availabilityService.getWeeklyUsageByStationAndConnector(stationName)).thenReturn(responseDTOList);

        mockMvc.perform(get("/availability/station/weekly-usage")
                        .param("stationName", stationName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].connectorId", is("1")))
                .andExpect(jsonPath("$[0].weeklyUsage", hasSize(2)))
                .andExpect(jsonPath("$[0].weeklyUsage[0].dayOfWeek", is("MONDAY")))
                .andExpect(jsonPath("$[0].weeklyUsage[0].averageUsage", is(0.75)))
                .andExpect(jsonPath("$[0].weeklyUsage[1].dayOfWeek", is("TUESDAY")))
                .andExpect(jsonPath("$[0].weeklyUsage[1].averageUsage", is(0.80)))
                .andExpect(jsonPath("$[1].connectorId", is("2")))
                .andExpect(jsonPath("$[1].weeklyUsage", hasSize(2)))
                .andExpect(jsonPath("$[1].weeklyUsage[0].dayOfWeek", is("MONDAY")))
                .andExpect(jsonPath("$[1].weeklyUsage[0].averageUsage", is(0.70)))
                .andExpect(jsonPath("$[1].weeklyUsage[1].dayOfWeek", is("TUESDAY")))
                .andExpect(jsonPath("$[1].weeklyUsage[1].averageUsage", is(0.85)));

        verify(availabilityService, times(1)).getWeeklyUsageByStationAndConnector(stationName);
    }

    // Tests for getWeeklyUsageByCity

    @Test
    public void testGetWeeklyUsageByCity_Success() throws Exception {
        String stationName = "Station1";

        WeeklyCityUsageDTO usageDTO = new WeeklyCityUsageDTO();
        usageDTO.setDayOfWeek("MONDAY");
        usageDTO.setAverageUsage(0.75);

        CityUsageResponseDTO responseDTO = new CityUsageResponseDTO();
        responseDTO.setCityName("City1");
        responseDTO.setWeeklyUsage(Collections.singletonList(usageDTO));

        when(availabilityService.getWeeklyUsageByCity(stationName)).thenReturn(responseDTO);

        mockMvc.perform(get("/availability/city/weekly-usage")
                        .param("stationName", stationName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cityName", is("City1")))
                .andExpect(jsonPath("$.weeklyUsage", hasSize(1)))
                .andExpect(jsonPath("$.weeklyUsage[0].dayOfWeek", is("MONDAY")))
                .andExpect(jsonPath("$.weeklyUsage[0].averageUsage", is(0.75)));

        verify(availabilityService, times(1)).getWeeklyUsageByCity(stationName);
    }

    @Test
    public void testGetWeeklyUsageByCity_EmptyResult() throws Exception {
        String stationName = "NonExistentStation";

        CityUsageResponseDTO responseDTO = new CityUsageResponseDTO();
        responseDTO.setCityName("City1");
        responseDTO.setWeeklyUsage(Collections.emptyList());

        when(availabilityService.getWeeklyUsageByCity(stationName)).thenReturn(responseDTO);

        mockMvc.perform(get("/availability/city/weekly-usage")
                        .param("stationName", stationName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cityName", is("City1")))
                .andExpect(jsonPath("$.weeklyUsage", hasSize(0)));

        verify(availabilityService, times(1)).getWeeklyUsageByCity(stationName);
    }

    @Test
    public void testGetWeeklyUsageByCity_MultipleDays() throws Exception {
        String stationName = "Station1";

        WeeklyCityUsageDTO usageDTO1 = new WeeklyCityUsageDTO();
        usageDTO1.setDayOfWeek("MONDAY");
        usageDTO1.setAverageUsage(0.75);

        WeeklyCityUsageDTO usageDTO2 = new WeeklyCityUsageDTO();
        usageDTO2.setDayOfWeek("TUESDAY");
        usageDTO2.setAverageUsage(0.80);

        CityUsageResponseDTO responseDTO = new CityUsageResponseDTO();
        responseDTO.setCityName("City1");
        responseDTO.setWeeklyUsage(Arrays.asList(usageDTO1, usageDTO2));

        when(availabilityService.getWeeklyUsageByCity(stationName)).thenReturn(responseDTO);

        mockMvc.perform(get("/availability/city/weekly-usage")
                        .param("stationName", stationName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cityName", is("City1")))
                .andExpect(jsonPath("$.weeklyUsage", hasSize(2)))
                .andExpect(jsonPath("$.weeklyUsage[0].dayOfWeek", is("MONDAY")))
                .andExpect(jsonPath("$.weeklyUsage[0].averageUsage", is(0.75)))
                .andExpect(jsonPath("$.weeklyUsage[1].dayOfWeek", is("TUESDAY")))
                .andExpect(jsonPath("$.weeklyUsage[1].averageUsage", is(0.80)));

        verify(availabilityService, times(1)).getWeeklyUsageByCity(stationName);
    }

    @Test
    public void testGetWeeklyHourlyUsageByStationAndConnector_Success() throws Exception {
        String stationName = "Station1";

        HourlyUsageDTO hourlyUsageDTO = new HourlyUsageDTO();
        hourlyUsageDTO.setHour(9);
        hourlyUsageDTO.setAverageUsage(0.75);

        WeeklyHourlyUsageDTO weeklyHourlyUsageDTO = new WeeklyHourlyUsageDTO();
        weeklyHourlyUsageDTO.setDayOfWeek("MONDAY");
        weeklyHourlyUsageDTO.setHourlyUsage(Collections.singletonList(hourlyUsageDTO));

        ConnectorWeeklyHourlyUsageDTO responseDTO = new ConnectorWeeklyHourlyUsageDTO();
        responseDTO.setConnectorId("1");
        responseDTO.setWeeklyHourlyUsage(Collections.singletonList(weeklyHourlyUsageDTO));

        List<ConnectorWeeklyHourlyUsageDTO> responseDTOList = Collections.singletonList(responseDTO);

        when(availabilityService.getWeeklyHourlyUsageByStationAndConnector(stationName)).thenReturn(responseDTOList);

        mockMvc.perform(get("/availability/station/weekly-hourly-usage")
                        .param("stationName", stationName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].connectorId", is("1")))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage", hasSize(1)))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[0].dayOfWeek", is("MONDAY")))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[0].hourlyUsage", hasSize(1)))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[0].hourlyUsage[0].hour", is(9)))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[0].hourlyUsage[0].averageUsage", is(0.75)));

        verify(availabilityService, times(1)).getWeeklyHourlyUsageByStationAndConnector(stationName);
    }

    @Test
    public void testGetWeeklyHourlyUsageByStationAndConnector_EmptyResult() throws Exception {
        String stationName = "NonExistentStation";

        when(availabilityService.getWeeklyHourlyUsageByStationAndConnector(stationName)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/availability/station/weekly-hourly-usage")
                        .param("stationName", stationName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(availabilityService, times(1)).getWeeklyHourlyUsageByStationAndConnector(stationName);
    }

    @Test
    public void testGetWeeklyHourlyUsageByStationAndConnector_MultipleConnectors() throws Exception {
        String stationName = "Station1";

        HourlyUsageDTO hourlyUsageDTO1 = new HourlyUsageDTO();
        hourlyUsageDTO1.setHour(9);
        hourlyUsageDTO1.setAverageUsage(0.75);

        WeeklyHourlyUsageDTO weeklyHourlyUsageDTO1 = new WeeklyHourlyUsageDTO();
        weeklyHourlyUsageDTO1.setDayOfWeek("MONDAY");
        weeklyHourlyUsageDTO1.setHourlyUsage(Collections.singletonList(hourlyUsageDTO1));

        ConnectorWeeklyHourlyUsageDTO responseDTO1 = new ConnectorWeeklyHourlyUsageDTO();
        responseDTO1.setConnectorId("1");
        responseDTO1.setWeeklyHourlyUsage(Collections.singletonList(weeklyHourlyUsageDTO1));

        HourlyUsageDTO hourlyUsageDTO2 = new HourlyUsageDTO();
        hourlyUsageDTO2.setHour(10);
        hourlyUsageDTO2.setAverageUsage(0.80);

        WeeklyHourlyUsageDTO weeklyHourlyUsageDTO2 = new WeeklyHourlyUsageDTO();
        weeklyHourlyUsageDTO2.setDayOfWeek("MONDAY");
        weeklyHourlyUsageDTO2.setHourlyUsage(Collections.singletonList(hourlyUsageDTO2));

        ConnectorWeeklyHourlyUsageDTO responseDTO2 = new ConnectorWeeklyHourlyUsageDTO();
        responseDTO2.setConnectorId("2");
        responseDTO2.setWeeklyHourlyUsage(Collections.singletonList(weeklyHourlyUsageDTO2));

        List<ConnectorWeeklyHourlyUsageDTO> responseDTOList = Arrays.asList(responseDTO1, responseDTO2);

        when(availabilityService.getWeeklyHourlyUsageByStationAndConnector(stationName)).thenReturn(responseDTOList);

        mockMvc.perform(get("/availability/station/weekly-hourly-usage")
                        .param("stationName", stationName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].connectorId", is("1")))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[0].hourlyUsage[0].hour", is(9)))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[0].hourlyUsage[0].averageUsage", is(0.75)))
                .andExpect(jsonPath("$[1].connectorId", is("2")))
                .andExpect(jsonPath("$[1].weeklyHourlyUsage[0].hourlyUsage[0].hour", is(10)))
                .andExpect(jsonPath("$[1].weeklyHourlyUsage[0].hourlyUsage[0].averageUsage", is(0.80)));

        verify(availabilityService, times(1)).getWeeklyHourlyUsageByStationAndConnector(stationName);
    }

    @Test
    public void testGetWeeklyHourlyUsageByStationAndConnector_MultipleDays() throws Exception {
        String stationName = "Station1";

        HourlyUsageDTO hourlyUsageDTO1 = new HourlyUsageDTO();
        hourlyUsageDTO1.setHour(9);
        hourlyUsageDTO1.setAverageUsage(0.75);

        WeeklyHourlyUsageDTO weeklyHourlyUsageDTO1 = new WeeklyHourlyUsageDTO();
        weeklyHourlyUsageDTO1.setDayOfWeek("MONDAY");
        weeklyHourlyUsageDTO1.setHourlyUsage(Collections.singletonList(hourlyUsageDTO1));

        HourlyUsageDTO hourlyUsageDTO2 = new HourlyUsageDTO();
        hourlyUsageDTO2.setHour(10);
        hourlyUsageDTO2.setAverageUsage(0.80);

        WeeklyHourlyUsageDTO weeklyHourlyUsageDTO2 = new WeeklyHourlyUsageDTO();
        weeklyHourlyUsageDTO2.setDayOfWeek("TUESDAY");
        weeklyHourlyUsageDTO2.setHourlyUsage(Collections.singletonList(hourlyUsageDTO2));

        ConnectorWeeklyHourlyUsageDTO responseDTO = new ConnectorWeeklyHourlyUsageDTO();
        responseDTO.setConnectorId("1");
        responseDTO.setWeeklyHourlyUsage(Arrays.asList(weeklyHourlyUsageDTO1, weeklyHourlyUsageDTO2));

        List<ConnectorWeeklyHourlyUsageDTO> responseDTOList = Collections.singletonList(responseDTO);

        when(availabilityService.getWeeklyHourlyUsageByStationAndConnector(stationName)).thenReturn(responseDTOList);

        mockMvc.perform(get("/availability/station/weekly-hourly-usage")
                        .param("stationName", stationName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].connectorId", is("1")))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage", hasSize(2)))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[0].dayOfWeek", is("MONDAY")))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[0].hourlyUsage[0].hour", is(9)))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[0].hourlyUsage[0].averageUsage", is(0.75)))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[1].dayOfWeek", is("TUESDAY")))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[1].hourlyUsage[0].hour", is(10)))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[1].hourlyUsage[0].averageUsage", is(0.80)));

        verify(availabilityService, times(1)).getWeeklyHourlyUsageByStationAndConnector(stationName);
    }

    @Test
    public void testGetWeeklyHourlyUsageByStationAndConnector_MultipleHours() throws Exception {
        String stationName = "Station1";

        HourlyUsageDTO hourlyUsageDTO1 = new HourlyUsageDTO();
        hourlyUsageDTO1.setHour(9);
        hourlyUsageDTO1.setAverageUsage(0.75);

        HourlyUsageDTO hourlyUsageDTO2 = new HourlyUsageDTO();
        hourlyUsageDTO2.setHour(10);
        hourlyUsageDTO2.setAverageUsage(0.80);

        WeeklyHourlyUsageDTO weeklyHourlyUsageDTO = new WeeklyHourlyUsageDTO();
        weeklyHourlyUsageDTO.setDayOfWeek("MONDAY");
        weeklyHourlyUsageDTO.setHourlyUsage(Arrays.asList(hourlyUsageDTO1, hourlyUsageDTO2));

        ConnectorWeeklyHourlyUsageDTO responseDTO = new ConnectorWeeklyHourlyUsageDTO();
        responseDTO.setConnectorId("1");
        responseDTO.setWeeklyHourlyUsage(Collections.singletonList(weeklyHourlyUsageDTO));

        List<ConnectorWeeklyHourlyUsageDTO> responseDTOList = Collections.singletonList(responseDTO);

        when(availabilityService.getWeeklyHourlyUsageByStationAndConnector(stationName)).thenReturn(responseDTOList);

        mockMvc.perform(get("/availability/station/weekly-hourly-usage")
                        .param("stationName", stationName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].connectorId", is("1")))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage", hasSize(1)))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[0].dayOfWeek", is("MONDAY")))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[0].hourlyUsage", hasSize(2)))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[0].hourlyUsage[0].hour", is(9)))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[0].hourlyUsage[0].averageUsage", is(0.75)))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[0].hourlyUsage[1].hour", is(10)))
                .andExpect(jsonPath("$[0].weeklyHourlyUsage[0].hourlyUsage[1].averageUsage", is(0.80)));

        verify(availabilityService, times(1)).getWeeklyHourlyUsageByStationAndConnector(stationName);
    }
}