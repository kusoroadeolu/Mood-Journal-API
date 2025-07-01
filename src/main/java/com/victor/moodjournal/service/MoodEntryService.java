package com.victor.moodjournal.service;

import com.victor.moodjournal.dto.MoodEntryDto;
import com.victor.moodjournal.dto.MoodEntryResponseDto;
import com.victor.moodjournal.dto.MoodEntryUpdateDto;
import com.victor.moodjournal.dto.MoodStatsDto;
import com.victor.moodjournal.model.Mood;
import com.victor.moodjournal.model.MoodEntry;
import com.victor.moodjournal.model.User;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service

public interface MoodEntryService {

    User validateUser(Long id);
    MoodEntry createMoodEntry(MoodEntryDto moodEntryDto, Long userId);
    MoodEntryResponseDto updateMoodEntry(Long userId, Long moodEntryId, MoodEntryUpdateDto updateDto);
    void deleteMoodEntry(Long id);
    Page<MoodEntryResponseDto> findMoodEntriesByUser(Long userId, LocalDate start, LocalDate end, int page, int size);
    Page<MoodEntryResponseDto> findMoodEntriesByUserAndMood(Long userId, Mood mood, int page, int size);
    MoodEntryResponseDto findMoodEntryByUserAndCreatedDate(Long userId, LocalDate date);
    MoodStatsDto getMoodStatsBetween(Long userId, LocalDate start, LocalDate end);
    MoodEntryResponseDto findMoodEntryById(Long id);
}
