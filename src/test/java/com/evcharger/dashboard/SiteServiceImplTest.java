package com.evcharger.dashboard;

import com.evcharger.dashboard.mapper.SiteMapper;
import com.evcharger.dashboard.entity.Site;
import com.evcharger.dashboard.service.impl.SiteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SiteServiceImplTest {

    @Mock
    private SiteMapper siteMapper;

    @InjectMocks
    private SiteServiceImpl siteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUniqueCityIds() {
        // Arrange
        List<String> expectedCityIds = Arrays.asList("city1", "city2", "city3");
        when(siteMapper.getAllUniqueCityIds()).thenReturn(expectedCityIds);

        // Act
        List<String> actualCityIds = siteService.getAllUniqueCityIds1();

        // Assert
        assertEquals(expectedCityIds, actualCityIds);
        verify(siteMapper, times(1)).getAllUniqueCityIds();
    }

    @Test
    void testSave() {
        // Arrange
        Site site = new Site();
        site.setSiteId("site1");
        site.setStreet("Main Street");
        site.setPostcode("12345");
        site.setCoordinatesX(1.23);
        site.setCoordinatesY(4.56);
        site.setCityId("city1");

        when(siteMapper.insert(site)).thenReturn(1);

        // Act
        boolean result = siteService.save(site);

        // Assert
        assertTrue(result);
        verify(siteMapper, times(1)).insert(site);
    }

    @Test
    void testGetById() {
        // Arrange
        String siteId = "site1";
        Site expectedSite = new Site();
        expectedSite.setSiteId(siteId);
        expectedSite.setStreet("Main Street");
        expectedSite.setPostcode("12345");
        expectedSite.setCoordinatesX(1.23);
        expectedSite.setCoordinatesY(4.56);
        expectedSite.setCityId("city1");

        when(siteMapper.selectById(siteId)).thenReturn(expectedSite);

        // Act
        Site actualSite = siteService.getById(siteId);

        // Assert
        assertEquals(expectedSite, actualSite);
        verify(siteMapper, times(1)).selectById(siteId);
    }
}