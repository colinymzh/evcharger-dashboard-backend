package com.evcharger.dashboard.service.impl;

import com.evcharger.dashboard.entity.dto.SiteCoordinates;
import com.evcharger.dashboard.mapper.PredictionInputMapper;
import com.evcharger.dashboard.service.PredictionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PredictionServiceImpl implements PredictionService {

    @Autowired
    private PredictionInputMapper predictionInputMapper;

    private static final String API_KEY = "404ae755d2b7cb225bad7f0098968ae2";

    @Override
    public String getWeatherPrediction(String stationName, String date, int dayOfWeek, int hour) {
        // 获取coordinates_x和coordinates_y
        SiteCoordinates coordinates = predictionInputMapper.getCoordinatesByStationName(stationName);
        double coordinatesX = coordinates.getCoordinatesX();
        double coordinatesY = coordinates.getCoordinatesY();

        // 计算cnt
        LocalDate currentDate = LocalDate.now();
        LocalDate targetDate = LocalDate.parse(date);
        long cnt = ChronoUnit.DAYS.between(currentDate, targetDate);

        // 调用天气API
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/forecast/daily?lat=%f&lon=%f&cnt=%d&appid=%s",
                coordinatesY, coordinatesX, cnt, API_KEY
        );

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        // 解析response
        ObjectMapper mapper = new ObjectMapper();
        String weather = "";
        try {
            JsonNode root = mapper.readTree(response);
            JsonNode list = root.path("list");
            if (list.isArray() && list.size() > 0) {
                JsonNode lastItem = list.get(list.size() - 1);
                JsonNode weatherNode = lastItem.path("weather").get(0);
                weather = weatherNode.path("main").asText();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return weather;
    }
}
