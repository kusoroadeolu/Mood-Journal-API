package com.victor.moodjournal.mapper;

import com.victor.moodjournal.dto.MoodEntryDto;
import com.victor.moodjournal.dto.MoodEntryResponseDto;
import com.victor.moodjournal.model.MoodEntry;
import com.victor.moodjournal.model.User;
import com.victor.moodjournal.model.UserMood;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class MoodEntryMapperTest {

    @InjectMocks
    private MoodEntryMapper moodEntryMapper;


    private MoodEntryDto entryDto;
    private MoodEntry moodEntry;
    private User user;

    @BeforeEach
    public void setUp(){
        //given
        user = new User();
        UserMood userMood = new UserMood();
        entryDto = new MoodEntryDto(
                userMood,
                "test_note",
                "test_tag"
        );

        moodEntry = new MoodEntry(
                userMood,
                "test_note",
                "test_tag",
                user
        );
    }

    @Test
    public void should_map_mood_entry_dto_to_mood_entry(){
        //when
        MoodEntry entry = moodEntryMapper.toMoodEntry(entryDto, user);

        //then
        assertNotNull(entry);
        assertNotNull(entry.getUser());
        assertEquals(entryDto.tag(), entry.getTag());
        assertEquals(entryDto.note(), entry.getNote());
        assertEquals(entryDto.userMood(), entry.getUserMood());

    }

    @Test
    public void should_map_mood_entry_to_response_dto(){
        //when
        MoodEntryResponseDto responseDto = moodEntryMapper.toMoodEntryResponse(moodEntry);

        //then
        assertNotNull(responseDto);
        assertEquals(moodEntry.getUserMood().getUserEmoji(), responseDto.moodEmoji());
        assertEquals(moodEntry.getNote(), responseDto.note());
        assertEquals(moodEntry.getCreatedDate().toString(), responseDto.createdAt());

    }
}