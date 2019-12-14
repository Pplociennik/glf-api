package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.Stats;
import com.goaleaf.repositories.StatsRepository;
import com.goaleaf.services.StatsService;
import org.joda.time.DateTimeComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private StatsRepository statsRepository;

    @Override
    public Stats findStatsByDate(Date date) {
        Iterable<Stats> list = statsRepository.findAll();
        DateTimeComparator comparator = DateTimeComparator.getDateOnlyInstance();
        Stats result = null;

        if (!list.iterator().hasNext()) {
            return null;
        }

        for (Stats s : list) {
            if (comparator.compare(s.getDay(), date) == 0) {
                result = s;
            }
        }
        return result;
    }

    @Override
    public Stats save(Stats stats) {
        return statsRepository.save(stats);
    }

    @Override
    public Iterable<Stats> findAllStats() {
        return statsRepository.findAll();
    }
}
