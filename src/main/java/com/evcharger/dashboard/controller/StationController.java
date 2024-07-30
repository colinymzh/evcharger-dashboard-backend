package com.evcharger.dashboard.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.evcharger.dashboard.entity.Station;
import com.evcharger.dashboard.entity.dto.StationDetailDTO;
import com.evcharger.dashboard.entity.dto.StationSiteDTO;
import com.evcharger.dashboard.entity.dto.StationUsageDTO;
import com.evcharger.dashboard.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
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

//    @GetMapping("/filtered")
//    public IPage<StationSiteDTO> getFilteredStations(@RequestParam("page") int page,
//                                                     @RequestParam("size") int size,
//                                                     @RequestParam(value = "stationName", required = false) String stationName,
//                                                     @RequestParam(value = "city", required = false) String city,
//                                                     @RequestParam(value = "postcode", required = false) String postcode,
//                                                     @RequestParam(value = "supportsFastCharging", required = false) Boolean supportsFastCharging) {
//        Page<StationSiteDTO> pagination = new Page<>(page, size);
//        return stationService.getStationsWithFilters(pagination, stationName, city, postcode, supportsFastCharging);
//    }
@GetMapping("/filtered")
public IPage<StationSiteDTO> getFilteredStations(@RequestParam("page") int page,
                                                 @RequestParam("size") int size,
                                                 @RequestParam(value = "stationName", required = false) String stationName,
                                                 @RequestParam(value = "cityName", required = false) String city,
                                                 @RequestParam(value = "postcode", required = false) String postcode,
                                                 @RequestParam(value = "supportsFastCharging", required = false) Boolean supportsFastCharging) {
    Page<StationSiteDTO> pagination = new Page<>(page, size);
    return stationService.getStationsWithFilters(pagination, stationName, city, postcode, supportsFastCharging);
}

    @GetMapping("/{stationName}")
    public StationDetailDTO getStationDetails(@PathVariable("stationName") String stationName) {
        return stationService.getStationDetails(stationName);
    }

    @GetMapping("/usage")
    public ResponseEntity<List<StationUsageDTO>> getStationsUsage() {
        List<StationUsageDTO> stationUsage = stationService.getStationsUsageWithLocation();
        return ResponseEntity.ok(stationUsage);
    }


}
