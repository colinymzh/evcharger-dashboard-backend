package com.evcharger.dashboard.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.evcharger.dashboard.entity.Connector;
import com.evcharger.dashboard.entity.Station;
import com.evcharger.dashboard.entity.StationSiteDTO;
import com.evcharger.dashboard.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {

    @Autowired
    private StationService stationService;

    @GetMapping
    public List<Station> getAllStations() {
        return stationService.list();
    }

    @PostMapping
    public boolean addStation(@RequestBody Station station) {
        return stationService.save(station);
    }

    @PutMapping
    public boolean updateStation(@RequestBody Station station) {
        return stationService.updateById(station);
    }

    @DeleteMapping("/{id}")
    public boolean deleteStation(@PathVariable("id") String id) {
        return stationService.removeById(id);
    }

    @GetMapping("/hello")
    public String getAllConnectors() {
        return "ok了家人们";
    }
    @GetMapping("/homepage")
    public IPage<StationSiteDTO> getPagedStations(@RequestParam("page") int page,
                                                  @RequestParam("size") int size) {
        Page<StationSiteDTO> pagination = new Page<>(page, size);
        return stationService.getStationsWithSite(pagination);
    }
}
