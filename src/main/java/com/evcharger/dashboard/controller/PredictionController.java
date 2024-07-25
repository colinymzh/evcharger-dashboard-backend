package com.evcharger.dashboard.controller;

import com.evcharger.dashboard.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prediction")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @GetMapping()
    public String getWeatherPrediction(@RequestParam("stationName") String stationName,
                                       @RequestParam("date") String date,
                                       @RequestParam("dayOfWeek") int dayOfWeek,
                                       @RequestParam("hour") int hour) {
        return predictionService.getWeatherPrediction(stationName, date, dayOfWeek, hour);
    }
}
