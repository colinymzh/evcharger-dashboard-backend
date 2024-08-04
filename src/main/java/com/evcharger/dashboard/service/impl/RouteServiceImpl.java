package com.evcharger.dashboard.service.impl;

import com.evcharger.dashboard.entity.dto.RouteSummaryDTO;
import com.evcharger.dashboard.entity.dto.SiteCoordinates;
import com.evcharger.dashboard.mapper.SiteMapper;
import com.evcharger.dashboard.mapper.StationMapper;
import com.evcharger.dashboard.service.RouteService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class RouteServiceImpl implements RouteService {

    @Autowired
    private StationMapper stationMapper;

    @Autowired
    private SiteMapper siteMapper;

    /**
     * Fetches the route summary from start coordinates to a specific station.
     *
     * @param startX      The starting X coordinate (longitude).
     * @param startY      The starting Y coordinate (latitude).
     * @param stationName The name of the destination station.
     * @return A RouteSummaryDTO containing distance and duration.
     */
    @Override
    public RouteSummaryDTO getRouteSummary(double startX, double startY, String stationName) {
        // Retrieve site ID for the given station name
        Integer siteId = stationMapper.getSiteIdByStationName(stationName);

        // Retrieve destination coordinates
        SiteCoordinates coordinates = siteMapper.getCoordinatesBySiteId(siteId);
        double endX = coordinates.getCoordinatesX();
        double endY = coordinates.getCoordinatesY();

        // OpenRouteService API key
        String apiKey = "5b3ce3597851110001cf62483aa6fa61318843e9a8acbc8377bba6df";

        // Construct API URL for route calculation
        String url = String.format(
                "https://api.openrouteservice.org/v2/directions/driving-car?api_key=%s&start=%f,%f&end=%f,%f",
                apiKey, startX, startY, endX, endY
        );

        // Initialize RestTemplate for making HTTP requests
        RestTemplate restTemplate = new RestTemplate();
        RouteSummaryDTO summary = new RouteSummaryDTO();
        try {
            // Create URI object for the API request
            URI uri = new URI(url);

            // Send GET request to the OpenRouteService API
            String response = restTemplate.getForObject(uri, String.class);

            // Parse JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode summaryNode = root.path("features").get(0).path("properties").path("summary");

            // Extract distance and duration from JSON response
            summary.setDistance(summaryNode.path("distance").asDouble());
            summary.setDuration(summaryNode.path("duration").asDouble());

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            // Consider adding proper logging or error handling here
        }

        return summary;
    }
}
