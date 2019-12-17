package com.goaleaf.services;

import com.goaleaf.entities.DTO.StatsDTO;
import com.goaleaf.entities.Stats;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public interface StatsService {

    Stats findStatsByDate(Date date);

    Stats save(Stats stats);

    Iterable<StatsDTO> findAllStats();
}
