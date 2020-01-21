package com.goaleaf.controllers;

import com.goaleaf.entities.DTO.CountsDTO;
import com.goaleaf.entities.DTO.StatsDTO;
import com.goaleaf.services.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/stats")
@CrossOrigin(value = "*", maxAge = 3600)
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/all")
    public Iterable<StatsDTO> getAllStats() {
        return statsService.findAllStats();
    }

    @GetMapping("/sum")
    public StatsDTO sumAllStats() {
        return statsService.sumAllStats();
    }

    @GetMapping("/counts")
    public CountsDTO getCounts() {
        return statsService.getAllCounts();
    }
}
