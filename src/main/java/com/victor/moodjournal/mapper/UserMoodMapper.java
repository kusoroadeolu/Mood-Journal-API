package com.victor.moodjournal.mapper;

import com.victor.moodjournal.dto.UserMoodDto;
import com.victor.moodjournal.model.Mood;
import com.victor.moodjournal.model.UserMood;
import org.springframework.stereotype.Service;

@Service
public class UserMoodMapper {
    public UserMood toUserMood(UserMoodDto userMoodDto){
        return new UserMood(
                userMoodDto.mood(),
                userMoodDto.emoji()
        );
    }
}
