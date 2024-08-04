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


}
