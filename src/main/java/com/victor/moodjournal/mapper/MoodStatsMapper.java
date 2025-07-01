package com.victor.moodjournal.mapper;

import com.victor.moodjournal.dto.MoodStatsDto;
import com.victor.moodjournal.model.Mood;
import org.springframework.stereotype.Service;

@Service
public class MoodStatsMapper {
    public MoodStatsDto mapMoodStats(Mood frequentMood, Mood averageMood, long numberOfEntries){
        return new MoodStatsDto(
                frequentMood,
                averageMood,
                numberOfEntries
        );
    }
}
