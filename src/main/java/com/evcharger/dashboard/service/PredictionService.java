package com.evcharger.dashboard.service;

public interface PredictionService {
    String getWeatherPrediction(String stationName, String date, int dayOfWeek, int hour);
}
