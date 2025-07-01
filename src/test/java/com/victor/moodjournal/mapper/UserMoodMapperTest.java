package com.victor.moodjournal.mapper;

import com.victor.moodjournal.dto.UserMoodDto;
import com.victor.moodjournal.model.Mood;
import com.victor.moodjournal.model.UserMood;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMoodMapperTest {

    @InjectMocks
    private UserMoodMapper userMoodMapper;

    @Test
    public void should_map_user_mood_dto_to_user_mood(){
        //given
        UserMoodDto userMoodDto = new UserMoodDto(
                Mood.ANGRY,
                Mood.ANGRY.getEmoji()
        );

        //when
        UserMood userMood = userMoodMapper.toUserMood(userMoodDto);

        //then
        assertNotNull(userMood);
        assertEquals(userMoodDto.mood(), userMood.getUserMood());
        assertEquals(userMoodDto.emoji(), userMood.getUserEmoji());
    }
}