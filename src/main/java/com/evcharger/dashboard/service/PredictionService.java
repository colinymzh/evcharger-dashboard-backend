package com.evcharger.dashboard.service;

import java.util.List;

public interface PredictionService {
    List<String> getWeatherPrediction(String stationName, String date, int dayOfWeek, int hour);
}
