package com.goaleaf.controllers;

import com.goaleaf.entities.Stats;
import com.goaleaf.services.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/all")
    public Iterable<Stats> getAllStats() {
        return statsService.findAllStats();
    }
}
