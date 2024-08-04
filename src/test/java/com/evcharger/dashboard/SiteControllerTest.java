package com.evcharger.dashboard;

import com.evcharger.dashboard.controller.SiteController;
import com.evcharger.dashboard.service.CityService;
import com.evcharger.dashboard.service.SiteService;
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

public class SiteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SiteService siteService;

    @Mock
    private CityService cityService;

    @InjectMocks
    private SiteController siteController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(siteController).build();
    }

    @Test
    public void testGetAllUniqueCities_Success() throws Exception {
        List<String> cityIds = Arrays.asList("1", "2", "3");
        when(siteService.getAllUniqueCityIds()).thenReturn(cityIds);
        when(cityService.getCityNameById("1")).thenReturn("New York");
        when(cityService.getCityNameById("2")).thenReturn("Los Angeles");
        when(cityService.getCityNameById("3")).thenReturn("Chicago");

        mockMvc.perform(get("/sites/cities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("New York")))
                .andExpect(jsonPath("$[1]", is("Los Angeles")))
                .andExpect(jsonPath("$[2]", is("Chicago")));

        verify(siteService, times(1)).getAllUniqueCityIds();
        verify(cityService, times(3)).getCityNameById(anyString());
    }

    @Test
    public void testGetAllUniqueCities_EmptyResult() throws Exception {
        when(siteService.getAllUniqueCityIds()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/sites/cities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(siteService, times(1)).getAllUniqueCityIds();
        verify(cityService, never()).getCityNameById(anyString());
    }

    @Test
    public void testGetAllUniqueCities_SingleCity() throws Exception {
        List<String> cityIds = Collections.singletonList("1");
        when(siteService.getAllUniqueCityIds()).thenReturn(cityIds);
        when(cityService.getCityNameById("1")).thenReturn("New York");

        mockMvc.perform(get("/sites/cities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", is("New York")));

        verify(siteService, times(1)).getAllUniqueCityIds();
        verify(cityService, times(1)).getCityNameById("1");
    }

    @Test
    public void testGetAllUniqueCities_NullCityName() throws Exception {
        List<String> cityIds = Arrays.asList("1", "2");
        when(siteService.getAllUniqueCityIds()).thenReturn(cityIds);
        when(cityService.getCityNameById("1")).thenReturn("New York");
        when(cityService.getCityNameById("2")).thenReturn(null);

        mockMvc.perform(get("/sites/cities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", is("New York")))
                .andExpect(jsonPath("$[1]", nullValue()));

        verify(siteService, times(1)).getAllUniqueCityIds();
        verify(cityService, times(2)).getCityNameById(anyString());
    }

    @Test
    public void testGetAllUniqueCities_DuplicateCityIds() throws Exception {
        List<String> cityIds = Arrays.asList("1", "2", "1");
        when(siteService.getAllUniqueCityIds()).thenReturn(cityIds);
        when(cityService.getCityNameById("1")).thenReturn("New York");
        when(cityService.getCityNameById("2")).thenReturn("Los Angeles");

        mockMvc.perform(get("/sites/cities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("New York")))
                .andExpect(jsonPath("$[1]", is("Los Angeles")))
                .andExpect(jsonPath("$[2]", is("New York")));

        verify(siteService, times(1)).getAllUniqueCityIds();
        verify(cityService, times(3)).getCityNameById(anyString());
    }
}