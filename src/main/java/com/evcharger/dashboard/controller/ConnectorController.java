package com.evcharger.dashboard.controller;

import com.evcharger.dashboard.entity.Connector;
import com.evcharger.dashboard.service.ConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/connectors")
public class ConnectorController {

    @Autowired
    private ConnectorService connectorService;

    @GetMapping
    public List<Connector> getAllConnectors() {
        return connectorService.list();
    }

    @PostMapping
    public boolean addConnector(@RequestBody Connector connector) {
        return connectorService.save(connector);
    }

    @PutMapping
    public boolean updateConnector(@RequestBody Connector connector) {
        return connectorService.updateById(connector);
    }

    @DeleteMapping("/{id}")
    public boolean deleteConnector(@PathVariable("id") String id) {
        return connectorService.removeById(id);
    }
}
