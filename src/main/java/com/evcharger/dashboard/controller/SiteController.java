package com.evcharger.dashboard.controller;

import com.evcharger.dashboard.entity.Site;
import com.evcharger.dashboard.service.CityService;
import com.evcharger.dashboard.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/sites")
public class SiteController {

    @Autowired
    private SiteService siteService;

    @Autowired
    private CityService cityService;




    @GetMapping("/cities")
    public List<String> getAllUniqueCities() {
        List<String> cityIds = siteService.getAllUniqueCityIds();
        return cityIds.stream().map(cityService::getCityNameById).collect(Collectors.toList());
    }
}
