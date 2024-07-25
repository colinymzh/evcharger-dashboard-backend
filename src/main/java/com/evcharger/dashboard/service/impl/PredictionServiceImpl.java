package com.evcharger.dashboard.service.impl;

import com.evcharger.dashboard.entity.dto.ConnectorData;
import com.evcharger.dashboard.entity.dto.SiteCoordinates;
import com.evcharger.dashboard.mapper.PredictionInputMapper;
import com.evcharger.dashboard.service.PredictionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class PredictionServiceImpl implements PredictionService {

    @Autowired
    private PredictionInputMapper predictionInputMapper;

    private static final String API_KEY = "404ae755d2b7cb225bad7f0098968ae2";

    @Override
    public List<String> getWeatherPrediction(String stationName, String date, int dayOfWeek, int hour) {
        // 获取connector信息
        List<ConnectorData> connectors = predictionInputMapper.getConnectorsByStationName(stationName);
        List<String> predictions = new ArrayList<>();

        // 获取坐标和日期差异
        SiteCoordinates coordinates = predictionInputMapper.getCoordinatesByStationName(stationName);
        double lat = coordinates.getCoordinatesX();
        double lon = coordinates.getCoordinatesY();
        LocalDate targetDate = LocalDate.parse(date);
        long cnt = ChronoUnit.DAYS.between(LocalDate.now(), targetDate);

        // 调用天气API
        RestTemplate restTemplate = new RestTemplate();
        String weatherApiUrl = String.format("https://api.openweathermap.org/data/2.5/forecast/daily?lat=%s&lon=%s&cnt=%d&appid=%s",
                lat, lon, cnt, API_KEY);
        String weatherResponse = restTemplate.getForObject(weatherApiUrl, String.class);

        ObjectMapper mapper = new ObjectMapper();
        String weather = null;
        try {
            JsonNode root = mapper.readTree(weatherResponse);
            JsonNode list = root.path("list");
            if (list.isArray()) {
                JsonNode lastDayWeather = list.get(list.size() - 1);
                weather = lastDayWeather.path("weather").get(0).path("main").asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (ConnectorData connector : connectors) {
            // 调用Python脚本
            try {
                ProcessBuilder pb = new ProcessBuilder("python", "src/main/resources/prediction.py",
                        "--stationName", connector.getStationName(),
                        "--connectorId", String.valueOf(connector.getConnectorId()),
                        "--coordinatesX", String.valueOf(connector.getCoordinatesX()),
                        "--coordinatesY", String.valueOf(connector.getCoordinatesY()),
                        "--tariffAmount", String.valueOf(connector.getTariffAmount()),
                        "--tariffConnectionfee", String.valueOf(connector.getTariffConnectionfee()),
                        "--maxChargerate", String.valueOf(connector.getMaxChargerate()),
                        "--plugTypeCcs", String.valueOf(connector.getPlugTypeCcs()),
                        "--plugTypeChademo", String.valueOf(connector.getPlugTypeChademo()),
                        "--plugTypeType2Plug", String.valueOf(connector.getPlugTypeType2Plug()),
                        "--connectorTypeAc", String.valueOf(connector.getConnectorTypeAc()),
                        "--connectorTypeAcControllerReceiver", String.valueOf(connector.getConnectorTypeAcControllerReceiver()),
                        "--connectorTypeRapid", String.valueOf(connector.getConnectorTypeRapid()),
                        "--connectorTypeUltraRapid", String.valueOf(connector.getConnectorTypeUltraRapid()),
                        "--connectorTypeICharging", String.valueOf(connector.getConnectorTypeICharging()),
                        "--connectorAvgUsage", String.valueOf(connector.getConnectorAvgUsage()),
                        "--stationAvgUsage", String.valueOf(connector.getStationAvgUsage()),
                        "--distanceToCenter", String.valueOf(connector.getDistanceToCenter()),
                        "--cityStationDensity", String.valueOf(connector.getCityStationDensity()),
                        "--stationConnectorCount", String.valueOf(connector.getStationConnectorCount()),
                        "--stationAvgMaxChargerate", String.valueOf(connector.getStationAvgMaxChargerate()),
                        "--stationDensity10km", String.valueOf(connector.getStationDensity10km()),
                        "--stationDensity1km", String.valueOf(connector.getStationDensity1km()),
                        "--stationDensity20km", String.valueOf(connector.getStationDensity20km()),
                        "--dayOfWeek", String.valueOf(dayOfWeek),
                        "--hour", String.valueOf(hour),
                        "--weather", weather
                );

                pb.redirectErrorStream(true);
                Process process = pb.start();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        predictions.add(line);
                    }
                }
                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return predictions;
    }
}
