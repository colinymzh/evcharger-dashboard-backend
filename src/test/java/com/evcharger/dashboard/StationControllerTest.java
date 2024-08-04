package com.evcharger.dashboard;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.evcharger.dashboard.controller.StationController;
import com.evcharger.dashboard.entity.dto.ConnectorDTO;
import com.evcharger.dashboard.entity.dto.StationDetailDTO;
import com.evcharger.dashboard.entity.dto.StationSiteDTO;
import com.evcharger.dashboard.entity.dto.StationUsageDTO;
import com.evcharger.dashboard.service.StationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

public class StationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StationService stationService;

    @InjectMocks
    private StationController stationController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(stationController).build();
    }

    @Test
    public void testGetPagedStations_Success() throws Exception {
        Page<StationSiteDTO> page = new Page<>(1, 10);
        StationSiteDTO station1 = new StationSiteDTO();
        station1.setStationName("Station 1");
        station1.setCityName("City 1");
        StationSiteDTO station2 = new StationSiteDTO();
        station2.setStationName("Station 2");
        station2.setCityName("City 2");
        page.setRecords(Arrays.asList(station1, station2));
        page.setTotal(2);

        when(stationService.getStationsWithSite(any())).thenReturn(page);

        mockMvc.perform(get("/stations/homepage")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records", hasSize(2)))
                .andExpect(jsonPath("$.records[0].stationName", is("Station 1")))
                .andExpect(jsonPath("$.records[1].stationName", is("Station 2")))
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.current", is(1)));

        verify(stationService, times(1)).getStationsWithSite(any());
    }

    @Test
    public void testGetPagedStations_EmptyPage() throws Exception {
        Page<StationSiteDTO> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        page.setTotal(0);

        when(stationService.getStationsWithSite(any())).thenReturn(page);

        mockMvc.perform(get("/stations/homepage")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records", hasSize(0)))
                .andExpect(jsonPath("$.total", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.current", is(1)));

        verify(stationService, times(1)).getStationsWithSite(any());
    }

    @Test
    public void testGetPagedStations_SecondPage() throws Exception {
        Page<StationSiteDTO> page = new Page<>(2, 5);
        StationSiteDTO station = new StationSiteDTO();
        station.setStationName("Station 6");
        station.setCityName("City 6");
        page.setRecords(Collections.singletonList(station));
        page.setTotal(6);

        when(stationService.getStationsWithSite(Mockito.<Page<StationSiteDTO>>any())).thenReturn(page);

        mockMvc.perform(get("/stations/homepage")
                        .param("page", "2")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records", hasSize(1)))
                .andExpect(jsonPath("$.records[0].stationName", is("Station 6")))
                .andExpect(jsonPath("$.total", is(6)))
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.current", is(2)));

        verify(stationService, times(1)).getStationsWithSite(any());
    }
    @Test
    public void testGetPagedStations_InvalidPage() throws Exception {
        Page<StationSiteDTO> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(Collections.emptyList());
        emptyPage.setTotal(0);

        when(stationService.getStationsWithSite(any())).thenReturn(emptyPage);

        mockMvc.perform(get("/stations/homepage")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records", hasSize(0)))
                .andExpect(jsonPath("$.total", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.current", is(1)));

        verify(stationService, times(1)).getStationsWithSite(any());
    }

    @Test
    public void testGetPagedStations_InvalidSize() throws Exception {
        Page<StationSiteDTO> defaultPage = new Page<>(1, 10);
        defaultPage.setRecords(Collections.emptyList());
        defaultPage.setTotal(0);

        when(stationService.getStationsWithSite(any())).thenReturn(defaultPage);

        mockMvc.perform(get("/stations/homepage")
                        .param("page", "1")
                        .param("size", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records", hasSize(0)))
                .andExpect(jsonPath("$.total", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.current", is(1)));

        verify(stationService, times(1)).getStationsWithSite(any());
    }
    //--------------------------------------------------------------

    @Test
    public void testGetFilteredStations_NoFilters() throws Exception {
        Page<StationSiteDTO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(createStationSiteDTO("Station1", "City1", "12345")));
        page.setTotal(1);

        when(stationService.getStationsWithFilters(any(), isNull(), isNull(), isNull(), isNull())).thenReturn(page);

        mockMvc.perform(get("/stations/filtered")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records", hasSize(1)))
                .andExpect(jsonPath("$.records[0].stationName", is("Station1")))
                .andExpect(jsonPath("$.total", is(1)));

        verify(stationService, times(1)).getStationsWithFilters(any(), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    public void testGetFilteredStations_WithStationName() throws Exception {
        Page<StationSiteDTO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(createStationSiteDTO("TestStation", "City1", "12345")));
        page.setTotal(1);

        when(stationService.getStationsWithFilters(any(), eq("TestStation"), isNull(), isNull(), isNull())).thenReturn(page);

        mockMvc.perform(get("/stations/filtered")
                        .param("page", "1")
                        .param("size", "10")
                        .param("stationName", "TestStation")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records", hasSize(1)))
                .andExpect(jsonPath("$.records[0].stationName", is("TestStation")));

        verify(stationService, times(1)).getStationsWithFilters(any(), eq("TestStation"), isNull(), isNull(), isNull());
    }

    @Test
    public void testGetFilteredStations_WithCityName() throws Exception {
        Page<StationSiteDTO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(createStationSiteDTO("Station1", "TestCity", "12345")));
        page.setTotal(1);

        when(stationService.getStationsWithFilters(any(), isNull(), eq("TestCity"), isNull(), isNull())).thenReturn(page);

        mockMvc.perform(get("/stations/filtered")
                        .param("page", "1")
                        .param("size", "10")
                        .param("cityName", "TestCity")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records", hasSize(1)))
                .andExpect(jsonPath("$.records[0].cityName", is("TestCity")));

        verify(stationService, times(1)).getStationsWithFilters(any(), isNull(), eq("TestCity"), isNull(), isNull());
    }

    @Test
    public void testGetFilteredStations_WithPostcode() throws Exception {
        Page<StationSiteDTO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(createStationSiteDTO("Station1", "City1", "54321")));
        page.setTotal(1);

        when(stationService.getStationsWithFilters(any(), isNull(), isNull(), eq("54321"), isNull())).thenReturn(page);

        mockMvc.perform(get("/stations/filtered")
                        .param("page", "1")
                        .param("size", "10")
                        .param("postcode", "54321")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records", hasSize(1)))
                .andExpect(jsonPath("$.records[0].postcode", is("54321")));

        verify(stationService, times(1)).getStationsWithFilters(any(), isNull(), isNull(), eq("54321"), isNull());
    }

    @Test
    public void testGetFilteredStations_WithSupportsFastCharging() throws Exception {
        Page<StationSiteDTO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(createStationSiteDTO("FastStation", "City1", "12345")));
        page.setTotal(1);

        when(stationService.getStationsWithFilters(any(), isNull(), isNull(), isNull(), eq(true))).thenReturn(page);

        mockMvc.perform(get("/stations/filtered")
                        .param("page", "1")
                        .param("size", "10")
                        .param("supportsFastCharging", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records", hasSize(1)))
                .andExpect(jsonPath("$.records[0].stationName", is("FastStation")));

        verify(stationService, times(1)).getStationsWithFilters(any(), isNull(), isNull(), isNull(), eq(true));
    }

    @Test
    public void testGetFilteredStations_WithAllFilters() throws Exception {
        Page<StationSiteDTO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(createStationSiteDTO("AllFiltersStation", "FilterCity", "11111")));
        page.setTotal(1);

        when(stationService.getStationsWithFilters(any(), eq("AllFiltersStation"), eq("FilterCity"), eq("11111"), eq(true))).thenReturn(page);

        mockMvc.perform(get("/stations/filtered")
                        .param("page", "1")
                        .param("size", "10")
                        .param("stationName", "AllFiltersStation")
                        .param("cityName", "FilterCity")
                        .param("postcode", "11111")
                        .param("supportsFastCharging", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records", hasSize(1)))
                .andExpect(jsonPath("$.records[0].stationName", is("AllFiltersStation")))
                .andExpect(jsonPath("$.records[0].cityName", is("FilterCity")))
                .andExpect(jsonPath("$.records[0].postcode", is("11111")));

        verify(stationService, times(1)).getStationsWithFilters(any(), eq("AllFiltersStation"), eq("FilterCity"), eq("11111"), eq(true));
    }

    @Test
    public void testGetFilteredStations_EmptyResult() throws Exception {
        Page<StationSiteDTO> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        page.setTotal(0);

        when(stationService.getStationsWithFilters(any(), eq("NonExistentStation"), isNull(), isNull(), isNull())).thenReturn(page);

        mockMvc.perform(get("/stations/filtered")
                        .param("page", "1")
                        .param("size", "10")
                        .param("stationName", "NonExistentStation")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records", hasSize(0)))
                .andExpect(jsonPath("$.total", is(0)));

        verify(stationService, times(1)).getStationsWithFilters(any(), eq("NonExistentStation"), isNull(), isNull(), isNull());
    }

    @Test
    public void testGetFilteredStations_SecondPage() throws Exception {
        Page<StationSiteDTO> page = new Page<>(2, 5);
        page.setRecords(Arrays.asList(createStationSiteDTO("Station6", "City2", "23456")));
        page.setTotal(6);

        when(stationService.getStationsWithFilters(any(), isNull(), isNull(), isNull(), isNull())).thenReturn(page);

        mockMvc.perform(get("/stations/filtered")
                        .param("page", "2")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records", hasSize(1)))
                .andExpect(jsonPath("$.records[0].stationName", is("Station6")))
                .andExpect(jsonPath("$.total", is(6)))
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.current", is(2)));

        verify(stationService, times(1)).getStationsWithFilters(any(), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    public void testGetFilteredStations_InvalidPage() throws Exception {
        mockMvc.perform(get("/stations/filtered")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(stationService, times(1)).getStationsWithFilters(any(), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    public void testGetFilteredStations_InvalidSize() throws Exception {
        mockMvc.perform(get("/stations/filtered")
                        .param("page", "1")
                        .param("size", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(stationService, times(1)).getStationsWithFilters(any(), isNull(), isNull(), isNull(), isNull());
    }

    private StationSiteDTO createStationSiteDTO(String stationName, String cityName, String postcode) {
        StationSiteDTO dto = new StationSiteDTO();
        dto.setStationName(stationName);
        dto.setCityName(cityName);
        dto.setPostcode(postcode);
        return dto;
    }
    //-----------------------------------------------------------------------------

    @Test
    public void testGetStationDetails_Success() throws Exception {
        StationDetailDTO stationDetail = createStationDetailDTO("TestStation", "City1", "12345");
        when(stationService.getStationDetails("TestStation")).thenReturn(stationDetail);

        mockMvc.perform(get("/stations/TestStation")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationName", is("TestStation")))
                .andExpect(jsonPath("$.cityName", is("City1")))
                .andExpect(jsonPath("$.postcode", is("12345")))
                .andExpect(jsonPath("$.connectors", hasSize(1)))
                .andExpect(jsonPath("$.connectors[0].connectorType", is("Type2")));

        verify(stationService, times(1)).getStationDetails("TestStation");
    }

    @Test
    public void testGetStationDetails_NotFound() throws Exception {
        when(stationService.getStationDetails("NonExistentStation")).thenReturn(null);

        mockMvc.perform(get("/stations/NonExistentStation")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(stationService, times(1)).getStationDetails("NonExistentStation");
    }

    @Test
    public void testGetStationDetails_WithoutConnectors() throws Exception {
        StationDetailDTO stationDetail = createStationDetailDTO("StationWithoutConnectors", "City2", "54321");
        stationDetail.setConnectors(null);
        when(stationService.getStationDetails("StationWithoutConnectors")).thenReturn(stationDetail);

        mockMvc.perform(get("/stations/StationWithoutConnectors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationName", is("StationWithoutConnectors")))
                .andExpect(jsonPath("$.cityName", is("City2")))
                .andExpect(jsonPath("$.postcode", is("54321")))
                .andExpect(jsonPath("$.connectors").doesNotExist());

        verify(stationService, times(1)).getStationDetails("StationWithoutConnectors");
    }

    private StationDetailDTO createStationDetailDTO(String stationName, String cityName, String postcode) {
        StationDetailDTO dto = new StationDetailDTO();
        dto.setStationName(stationName);
        dto.setCityName(cityName);
        dto.setPostcode(postcode);
        dto.setSiteId("SITE001");
        dto.setTariffAmount(0.5);
        dto.setTariffDescription("Per kWh");
        dto.setTariffConnectionfee(1.0);
        dto.setCityId("CITY001");
        dto.setStreet("Main Street");

        ConnectorDTO connector = new ConnectorDTO();
        connector.setConnectorType("Type2");
        connector.setMaxChargerate(22);
        dto.setConnectors(Arrays.asList(connector));

        return dto;
    }

    @Test
    public void testGetStationsUsage_Success() throws Exception {
        List<StationUsageDTO> stationUsages = Arrays.asList(
                createStationUsageDTO("Station1", 75.5, 3, 10.0, 20.0),
                createStationUsageDTO("Station2", 50.0, 2, 15.0, 25.0)
        );
        when(stationService.getStationsUsageWithLocation()).thenReturn(stationUsages);

        mockMvc.perform(get("/stations/usage")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].stationName", is("Station1")))
                .andExpect(jsonPath("$[0].averageUsage", is(75.5)))
                .andExpect(jsonPath("$[0].usageLevel", is(3)))
                .andExpect(jsonPath("$[0].coordinatesX", is(10.0)))
                .andExpect(jsonPath("$[0].coordinatesY", is(20.0)))
                .andExpect(jsonPath("$[1].stationName", is("Station2")));

        verify(stationService, times(1)).getStationsUsageWithLocation();
    }

    @Test
    public void testGetStationsUsage_EmptyList() throws Exception {
        when(stationService.getStationsUsageWithLocation()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/stations/usage")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(stationService, times(1)).getStationsUsageWithLocation();
    }

    @Test
    public void testGetStationsUsage_NullResponse() throws Exception {
        when(stationService.getStationsUsageWithLocation()).thenReturn(null);

        mockMvc.perform(get("/stations/usage")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(stationService, times(1)).getStationsUsageWithLocation();
    }

//    @Test
//    public void testGetStationsUsage_ServiceException() throws Exception {
//        when(stationService.getStationsUsageWithLocation()).thenThrow(new RuntimeException("Service error"));
//
//        mockMvc.perform(get("/stations/usage")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isInternalServerError());
//
//        verify(stationService, times(1)).getStationsUsageWithLocation();
//    }



    private StationUsageDTO createStationUsageDTO(String stationName, Double averageUsage, Integer usageLevel, Double coordinatesX, Double coordinatesY) {
        StationUsageDTO dto = new StationUsageDTO();
        dto.setStationName(stationName);
        dto.setAverageUsage(averageUsage);
        dto.setUsageLevel(usageLevel);
        dto.setCoordinatesX(coordinatesX);
        dto.setCoordinatesY(coordinatesY);
        return dto;
    }
}