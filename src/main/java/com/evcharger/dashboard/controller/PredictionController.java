package com.evcharger.dashboard.controller;

import com.evcharger.dashboard.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/prediction")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @GetMapping("/weather")
    public List<String> getWeatherPrediction(@RequestParam("stationName") String stationName,
                                             @RequestParam("date") String date,
                                             @RequestParam("dayOfWeek") int dayOfWeek,
                                             @RequestParam("hour") int hour) {
        return predictionService.getWeatherPrediction(stationName, date, dayOfWeek, hour);
    }
}
