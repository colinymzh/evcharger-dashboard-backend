package com.evcharger.dashboard;

import com.evcharger.dashboard.entity.City;
import com.evcharger.dashboard.mapper.CityMapper;
import com.evcharger.dashboard.service.impl.CityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CityServiceImplTest {

    @Mock
    private CityMapper cityMapper;

    @InjectMocks
    private CityServiceImpl cityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCityNameById_ExistingCity() {
        // Arrange
        String cityId = "1";
        String expectedCityName = "New York";
        when(cityMapper.getCityNameById(cityId)).thenReturn(expectedCityName);

        // Act
        String result = cityService.getCityNameById(cityId);

        // Assert
        assertEquals(expectedCityName, result);
        verify(cityMapper, times(1)).getCityNameById(cityId);
    }

    @Test
    void testGetCityNameById_NonExistentCity() {
        // Arrange
        String cityId = "999";
        when(cityMapper.getCityNameById(cityId)).thenReturn(null);

        // Act
        String result = cityService.getCityNameById(cityId);

        // Assert
        assertNull(result);
        verify(cityMapper, times(1)).getCityNameById(cityId);
    }

    @Test
    void testInsertCity() {
        // Arrange
        City city = new City();
        city.setCityId("2");
        city.setCityName("Los Angeles");
        when(cityMapper.insert(city)).thenReturn(1);

        // Act
        int result = cityMapper.insert(city);

        // Assert
        assertEquals(1, result);
        verify(cityMapper, times(1)).insert(city);
    }
}