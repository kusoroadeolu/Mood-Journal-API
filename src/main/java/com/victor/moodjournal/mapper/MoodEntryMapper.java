package com.victor.moodjournal.mapper;

import com.victor.moodjournal.dto.MoodEntryDto;
import com.victor.moodjournal.dto.MoodEntryResponseDto;
import com.victor.moodjournal.model.Mood;
import com.victor.moodjournal.model.MoodEntry;
import com.victor.moodjournal.model.User;
import com.victor.moodjournal.model.UserMood;
import com.victor.moodjournal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoodEntryMapper {

    public MoodEntry toMoodEntry(MoodEntryDto moodEntryDto, User user){
        return new MoodEntry(
                moodEntryDto.userMood(),
                moodEntryDto.note(),
                moodEntryDto.tag(),
                user
        );
    }

    public MoodEntryResponseDto toMoodEntryResponse(MoodEntry moodEntry){
        return new MoodEntryResponseDto(
                moodEntry.getUserMood().getUserEmoji(),
                moodEntry.getNote(),
                moodEntry.getCreatedDate().toString()
        );

    }
}
