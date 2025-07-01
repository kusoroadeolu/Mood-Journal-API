package com.victor.moodjournal.mapper;

import com.victor.moodjournal.dto.MoodStatsDto;
import com.victor.moodjournal.model.Mood;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MoodStatsMapperTest {
    @InjectMocks
    private MoodStatsMapper moodStatsMapper;

    @Test
    public void should_map_to_mood_stats(){
        //given
        Mood frequent = Mood.ANGRY;
        Mood average = Mood.SAD;
        long numberOfEntries = 10;

        //when
        MoodStatsDto moodStatsDto =
                moodStatsMapper.mapMoodStats(frequent, average, numberOfEntries);

        //then
        assertNotNull(moodStatsDto);
        assertEquals(frequent, moodStatsDto.frequentMood());
        assertEquals(average, moodStatsDto.averageMood());
        assertEquals(numberOfEntries, moodStatsDto.numberOfEntries());
    }
}