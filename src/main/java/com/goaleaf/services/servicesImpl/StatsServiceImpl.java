package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.DTO.StatsDTO;
import com.goaleaf.entities.Stats;
import com.goaleaf.repositories.StatsRepository;
import com.goaleaf.services.StatsService;
import org.joda.time.DateTimeComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public Iterable<StatsDTO> findAllStats() {
        return convertManyToDTOs(statsRepository.findAll());
    }

    private StatsDTO convertToDTO(Stats stats) {
        StatsDTO dto = new StatsDTO.StatsBuilder()
                .setCommentedPosts(stats.getCommentedPosts())
                .setCompletedTasks(stats.getCompletedTasks())
                .setCreatedAccounts(stats.getCreatedAccounts())
                .setCreatedChallenges(stats.getCreatedChallenges())
                .setCreatedPosts(stats.getCreatedPosts())
                .setCreatedTasks(stats.getCreatedTasks())
                .setDay(stats.getDay())
                .setFinishedChallenges(stats.getFinishedChallenges())
                .setInvitedMembers(stats.getInvitedMembers())
                .setLoggedUsers(stats.getLoggedUsers())
                .setSetGoals(stats.getSetGoals())
                .gainStats();
        return dto;
    }

    private Iterable<StatsDTO> convertManyToDTOs(Iterable<Stats> input) {
        List<StatsDTO> list = new ArrayList<>(0);

        for (Stats s : input) {
            list.add(this.convertToDTO(s));
        }

        return list;
    }
}
