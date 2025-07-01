package com.victor.moodjournal.service.impl;

import com.victor.moodjournal.dto.*;
import com.victor.moodjournal.exception.MoodEntryCreationException;
import com.victor.moodjournal.exception.MoodEntryNotFoundException;
import com.victor.moodjournal.exception.UserNotFoundException;
import com.victor.moodjournal.mapper.MoodEntryMapper;
import com.victor.moodjournal.mapper.MoodStatsMapper;
import com.victor.moodjournal.mapper.UserMoodMapper;
import com.victor.moodjournal.model.Mood;
import com.victor.moodjournal.model.MoodEntry;
import com.victor.moodjournal.model.User;
import com.victor.moodjournal.model.UserMood;
import com.victor.moodjournal.repository.MoodEntryRepository;
import com.victor.moodjournal.repository.UserRepository;
import com.victor.moodjournal.service.MoodEntryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoodEntryServiceImplTest {
    @Mock
    private MoodEntryMapper moodEntryMapper;

    @Mock
    private MoodEntryRepository moodEntryRepository;

    @Mock
    private MoodStatsMapper moodStatsMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMoodMapper userMoodMapper;

    @InjectMocks
    private MoodEntryServiceImpl moodEntryService;

    @Test
    public void should_return_a_user(){
        //before
        Long id = 1L;
        User user = User.builder()
                        .id(1L)
                        .username("Victor")
                        .email("victor@gmail.com")
                        .build();
        Optional<User> optionalUser = Optional.of(user);

        //When
        when(userRepository.findById(id)).thenReturn(optionalUser);

        User actualUser = moodEntryService.validateUser(id);

        //Then
        assertNotNull(actualUser);
        assertEquals(user.getId(), actualUser.getId());
        assertEquals(user.getUsername(), actualUser.getUsername());
    }

    @Test
    public void should_throw_exception_when_user_not_found(){
        //before
        Long id = 999L;

        //When
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class, () -> {
            moodEntryService.validateUser(id);
        });
    }

    @Test
    public void should_create_new_mood_entry(){
        //Given
        Long id = 1L;
        User user = User.builder()
                .id(1L)
                .username("Victor")
                .email("victor@gmail.com")
                .build();
        Optional<User> optionalUser = Optional.of(user);

        UserMood userMood = new UserMood(
                Mood.ANGRY,
                ""
        );

        MoodEntryDto dto = new MoodEntryDto(
                userMood,
                "Boss pissed me off",
                "Work"
        );

        MoodEntry entry = new MoodEntry(
                userMood,
                "Boss pissed me off",
                "Work",
                user
        );

        when(userRepository.findById(id)).thenReturn(optionalUser);
        when(moodEntryMapper.toMoodEntry(dto, user)).thenReturn(entry);
        when(moodEntryRepository.save(entry)).thenReturn(entry);

        //Then
        MoodEntry moodEntry = moodEntryService.createMoodEntry(dto, id);

        assertNotNull(moodEntry);
        assertEquals(
                entry.getUserMood().getUserMood().getEmoji(),
                moodEntry.getUserMood().getUserMood().getEmoji()
                );
        assertEquals(moodEntry.getTag(), entry.getTag());
        assertEquals(moodEntry.getNote(), entry.getNote());
        assertNotNull(moodEntry.getUpdatedAt());
    }

    @Test
    public void should_throw_exception_when_mood_entry_is_null(){
        Long id = 1L;
        User user = User.builder()
                .id(1L)
                .username("Victor")
                .email("victor@gmail.com")
                .build();
        Optional<User> optionalUser = Optional.of(user);

        UserMood userMood = new UserMood(
                Mood.ANGRY,
                ""
        );

        MoodEntryDto dto = new MoodEntryDto(
                userMood,
                "Boss pissed me off",
                "Work"
        );

        when(userRepository.findById(id)).thenReturn(optionalUser);
        when(moodEntryMapper.toMoodEntry(dto, user)).thenReturn(null);

        assertThrows(MoodEntryNotFoundException.class, () -> {
           moodEntryService.createMoodEntry(dto, id);
        });
    }

    @Test
    public void should_update_a_mood_entry(){
        Long moodId = 1L;

        User user = User.builder()
                .id(1L)
                .username("Victor")
                .email("victor@gmail.com")
                .build();

        UserMood userMood = new UserMood(
                Mood.ANGRY,
                ""
        );

        MoodEntry moodEntry1 = new MoodEntry(
                userMood,
                "Boss pissed me off",
                "Work",
                user

        );

        UserMood userMood2 = new UserMood(
                Mood.SAD,
                ""
        );

        UserMoodDto userMoodDto = new UserMoodDto(
                Mood.SAD,
                ""
        );

        MoodEntry moodEntry2 = new MoodEntry(
                userMood2,
                "Tired from work",
                "Work",
                user
        );

        MoodEntryResponseDto responseDto = new MoodEntryResponseDto(
                Mood.SAD.getEmoji(),
                "Tired from work",
                LocalDateTime.now().toString()
        );

        MoodEntryUpdateDto updateDto = new MoodEntryUpdateDto(
                "Tired from work",
                userMoodDto,
                "Work"
        );

        //When
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(moodEntryRepository.findByIdAndUserId(user.getId(), moodId))
                .thenReturn(Optional.of(moodEntry1));
        when(userMoodMapper.toUserMood(userMoodDto)).thenReturn(userMood2);
        when(moodEntryRepository.save(any(MoodEntry.class))).thenReturn(moodEntry2);
        when(moodEntryMapper.toMoodEntryResponse(moodEntry2)).thenReturn(responseDto);

        //Then
        MoodEntryResponseDto responseDto1 =
                moodEntryService.updateMoodEntry(user.getId(), moodId, updateDto);

        assertEquals(updateDto.note(), responseDto1.note());
        assertEquals(updateDto.userMoodDto().mood().getEmoji(), responseDto1.moodEmoji());
    }

    @Test
    public void should_throw_creation_exception_when_created_at_is_not_that_day(){
        Long moodId = 1L;

        User user = User.builder()
                .id(1L)
                .username("Victor")
                .email("victor@gmail.com")
                .build();

        UserMood userMood = new UserMood(
                Mood.ANGRY,
                ""
        );

        MoodEntry moodEntry1 = new MoodEntry(
                userMood,
                "Boss pissed me off",
                "Work",
                user

        );
        moodEntry1.setCreatedDate(LocalDate.now().plusDays(2));

        UserMoodDto userMoodDto = new UserMoodDto(
                Mood.SAD,
                ""
        );

        MoodEntryUpdateDto updateDto = new MoodEntryUpdateDto(
                "Tired from work",
                userMoodDto,
                "Work"
        );


        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(moodEntryRepository.findByIdAndUserId(user.getId(), moodId))
                .thenReturn(Optional.of(moodEntry1));

        //Then
       var creationEx = assertThrows(MoodEntryCreationException.class, () -> {
           moodEntryService.updateMoodEntry(user.getId(), moodId, updateDto);
        });
       assertEquals("Cannot update this mood entry, because it was not created today.", creationEx.getMessage());

    }

    @Test
    public void should_delete_a_mood_entry(){
        //Given
        Long moodId = 1L;

        User user = User.builder()
                .id(1L)
                .username("Victor")
                .email("victor@gmail.com")
                .build();

        UserMood userMood = new UserMood(
                Mood.ANGRY,
                ""
        );

        MoodEntry moodEntry1 = new MoodEntry(
                userMood,
                "Boss pissed me off",
                "Work",
                user

        );

        when(moodEntryRepository.findById(moodId)).thenReturn(Optional.of(moodEntry1));


        //then
        moodEntryService.deleteMoodEntry(moodId);

        verify(moodEntryRepository).delete(moodEntry1);

    }

    @Test
    public void should_throw_exception_when_deleting_non_existing_entry(){
        Long moodId = 999L;

        when(moodEntryRepository.findById(moodId)).thenReturn(Optional.empty());

        assertThrows(MoodEntryNotFoundException.class, () -> {
           moodEntryService.deleteMoodEntry(moodId);
        });

        verify(moodEntryRepository, never()).delete(any());
    }

    @Test
    public void should_return_entries_for_a_user(){
        //Given
        Long moodId = 1L;
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);
        int page = 1;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        User user = User.builder()
                .id(1L)
                .username("Victor")
                .email("victor@gmail.com")
                .build();

        UserMood userMood = new UserMood(
                Mood.ANGRY,
                ""
        );

        MoodEntry moodEntry1 = new MoodEntry(
                userMood,
                "Boss pissed me off",
                "Work",
                user
        );

        MoodEntry moodEntry2 = new MoodEntry(
                userMood,
                "Boss pissed on me",
                "Work",
                user
        );

        List<MoodEntry> entries = List.of(moodEntry1, moodEntry2);

        MoodEntryResponseDto responseDto1 = new MoodEntryResponseDto(
                moodEntry1.getUserMood().getUserEmoji(),
                moodEntry1.getNote(),
                moodEntry1.getCreatedAt().toString()
        );

        MoodEntryResponseDto responseDto2 = new MoodEntryResponseDto(
                moodEntry2.getUserMood().getUserEmoji(),
                moodEntry2.getNote(),
                moodEntry2.getCreatedAt().toString()
        );
        List<MoodEntryResponseDto> responseDtos = List.of(responseDto1, responseDto2);


        //when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(moodEntryRepository.findByUserIdAndCreatedDateBetween(user.getId() ,start, end, pageable))
                .thenReturn(new PageImpl<>(entries));
        when(moodEntryMapper.toMoodEntryResponse(moodEntry1)).thenReturn(responseDto1);
        when(moodEntryMapper.toMoodEntryResponse(moodEntry2)).thenReturn(responseDto2);

        //Then
        Page<MoodEntryResponseDto> moodEntryResponseDtos =
                moodEntryService.findMoodEntriesByUser(user.getId(), start, end, page, size);

        assertNotNull(moodEntryResponseDtos);
        verify(moodEntryRepository, atLeastOnce()).findByUserIdAndCreatedDateBetween(user.getId(), start, end, pageable);
        assertEquals(2, moodEntryResponseDtos.getContent().size());
        assertEquals("Boss pissed me off",
                moodEntryResponseDtos.getContent().getFirst().note());
        assertEquals("Boss pissed on me",
                moodEntryResponseDtos.getContent().get(1).note());
    }

    @Test
    public void should_find_mood_entry_by_user_and_date(){
        //Given

        LocalDate date = LocalDate.now();

        User user = User.builder()
                .id(1L)
                .username("Victor")
                .email("victor@gmail.com")
                .build();

        UserMood userMood = new UserMood(
                Mood.ANGRY,
                ""
        );

        MoodEntry moodEntry = new MoodEntry(
                userMood,
                "Boss pissed me off",
                "Work",
                user
        );

        MoodEntryResponseDto responseDto = new MoodEntryResponseDto(
                moodEntry.getUserMood().getUserEmoji(),
                moodEntry.getNote(),
                moodEntry.getCreatedAt().toString()
        );

        //When
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(moodEntryRepository.findByUserIdAndCreatedDate(user.getId(), date))
                .thenReturn(Optional.of(moodEntry));
        when(moodEntryMapper.toMoodEntryResponse(moodEntry)).thenReturn(responseDto);

        //Then
        MoodEntryResponseDto moodEntryResponseDto =
                moodEntryService.findMoodEntryByUserAndCreatedDate(user.getId(), date);

        assertNotNull(moodEntryResponseDto);
        assertEquals(moodEntry.getNote(), moodEntryResponseDto.note());
        assertEquals(moodEntry.getCreatedAt().toString(), moodEntryResponseDto.createdAt());
        assertEquals(moodEntry.getUserMood().getUserEmoji(), responseDto.moodEmoji());
    }

    @Test
    public void should_throw_mood_entry_exception_when_entry_does_not_exist(){

        //Given
        LocalDate date = LocalDate.now();
        User user = User.builder()
                .id(1L)
                .username("Victor")
                .email("victor@gmail.com")
                .build();

        UserMood userMood = new UserMood(
                Mood.ANGRY,
                ""
        );

        MoodEntry moodEntry = new MoodEntry(
                userMood,
                "Boss pissed me off",
                "Work",
                user
        );

        //When
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));


        //Then
        var ex = assertThrows(MoodEntryNotFoundException.class, () -> {
           moodEntryService.findMoodEntryByUserAndCreatedDate(user.getId(), date);
        });
        assertEquals("Could not find a mood entry created at date: " + date, ex.getMessage());
    }

    @Test
    public void should_find_mood_entry_by_id(){
        //Given
        Long moodId = 1L;
        User user = User.builder()
                .id(1L)
                .username("Victor")
                .email("victor@gmail.com")
                .build();

        UserMood userMood = new UserMood(
                Mood.ANGRY,
                ""
        );

        MoodEntry moodEntry = new MoodEntry(
                userMood,
                "Boss pissed me off",
                "Work",
                user
        );

        MoodEntryResponseDto responseDto = new MoodEntryResponseDto(
                moodEntry.getUserMood().getUserEmoji(),
                moodEntry.getNote(),
                moodEntry.getCreatedAt().toString()
        );

        //When
        when(moodEntryRepository.findById(moodId)).thenReturn(Optional.of(moodEntry));
        when(moodEntryMapper.toMoodEntryResponse(moodEntry)).thenReturn(responseDto);

        //Then
        MoodEntryResponseDto moodEntryResponseDto =
                moodEntryService.findMoodEntryById(moodId);

        assertNotNull(moodEntryResponseDto);
        verify(moodEntryMapper, atLeastOnce()).toMoodEntryResponse(moodEntry);
        assertEquals(moodEntry.getNote(), moodEntryResponseDto.note());
    }

    @Test
    public void should_get_mood_stats_between(){
        //Given
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(1);

        User user = User.builder()
                .id(1L)
                .username("Victor")
                .email("victor@gmail.com")
                .build();

        UserMood userMood = new UserMood(
                Mood.ANGRY,
                ""
        );

        MoodEntry moodEntry = new MoodEntry(
                userMood,
                "Boss pissed me off",
                "Work",
                user
        );

        Mood frequent = Mood.ANGRY;
        Mood average = Mood.ANGRY;
        long numberOfEntries = 5;

        MoodStatsDto statsDto = new MoodStatsDto(
                frequent,
                average,
                numberOfEntries
        );


        //when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(moodEntryRepository
                .findByUserIdAndCreatedDateBetween(user.getId(), start, end))
                .thenReturn(List.of(moodEntry));
        when(moodEntryRepository.countByUserIdAndCreatedDateBetween(user.getId(), start, end))
                .thenReturn(numberOfEntries);
        when(moodStatsMapper.mapMoodStats(frequent, average, numberOfEntries)).thenReturn(statsDto);

        //then
        MoodStatsDto moodStatsDto =
                moodEntryService.getMoodStatsBetween(user.getId(), start, end);
        verify(moodStatsMapper, atLeastOnce()).mapMoodStats(frequent, average, numberOfEntries);
        assertNotNull(moodStatsDto);
        assertEquals(statsDto.frequentMood().getEmoji(), moodStatsDto.frequentMood().getEmoji());
        assertEquals(statsDto.numberOfEntries(), moodStatsDto.numberOfEntries());

    }

    @Test
    public void should_find_mood_entries_by_user_and_mood(){
        User user = User.builder()
                .id(1L)
                .username("Victor")
                .email("victor@gmail.com")
                .build();

        UserMood userMood = new UserMood(
                Mood.ANGRY,
                ""
        );

        MoodEntry moodEntry = new MoodEntry(
                userMood,
                "Boss pissed me off",
                "Work",
                user
        );

        MoodEntryResponseDto responseDto = new MoodEntryResponseDto(
                moodEntry.getUserMood().getUserEmoji(),
                moodEntry.getNote(),
                moodEntry.getCreatedAt().toString()
        );

        Mood expectedMood = Mood.ANGRY;
        int page = 1;
        int size = 1;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        //when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(moodEntryRepository
                .findByUserIdAndUserMood(user.getId(), expectedMood, pageable))
                .thenReturn(new PageImpl<>(List.of(moodEntry)));
        when(moodEntryMapper.toMoodEntryResponse(moodEntry))
                .thenReturn(responseDto);

        Page<MoodEntryResponseDto> responseDtos =
                moodEntryService.findMoodEntriesByUserAndMood(user.getId(), expectedMood, page, size);

        verify(moodEntryRepository, atLeastOnce())
                .findByUserIdAndUserMood(user.getId(), expectedMood, pageable);
        assertNotNull(responseDtos);
        assertEquals(expectedMood.getEmoji(), responseDtos.getContent().getFirst().moodEmoji());
    }
}