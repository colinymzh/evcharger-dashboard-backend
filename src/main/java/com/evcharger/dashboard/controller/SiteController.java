package com.evcharger.dashboard.controller;

import com.evcharger.dashboard.entity.Site;
import com.evcharger.dashboard.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/sites")
public class SiteController {

    @Autowired
    private SiteService siteService;

    @GetMapping
    public List<Site> getAllSites() {
        return siteService.list();
    }

    @PostMapping
    public boolean addSite(@RequestBody Site site) {
        return siteService.save(site);
    }

    @PutMapping
    public boolean updateSite(@RequestBody Site site) {
        return siteService.updateById(site);
    }

    @DeleteMapping("/{id}")
    public boolean deleteSite(@PathVariable("id") String id) {
        return siteService.removeById(id);
    }


    @GetMapping("/cities")
    public List<String> getAllUniqueCities() {
        return siteService.getAllUniqueCities();
    }
}
