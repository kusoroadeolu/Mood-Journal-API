package com.victor.moodjournal.repository;


import com.victor.moodjournal.model.Mood;
import com.victor.moodjournal.model.MoodEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    Page<MoodEntry> findByUserId(Long userId, Pageable pageable);
    Optional<MoodEntry> findByIdAndUserId(Long userId, Long moodEntryId);
    Page<MoodEntry> findByUserIdAndCreatedDateBetween(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<MoodEntry> findByUserIdAndCreatedDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    long countByUserIdAndCreatedDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    Optional<MoodEntry> findByUserIdAndCreatedDate(Long userId, LocalDate date);

//    @Query("SELECT m FROM MoodEntry WHERE m.userMood.userMood = :mood")
    Page<MoodEntry> findByUserIdAndUserMood(Long userId, Mood mood, Pageable pageable);

    @Query(value = "SELECT COUNT(*) > 0 FROM mood_entry WHERE user_id = :userId AND DATE(created_at) = :date", nativeQuery = true)
    long findEntryCountByDate(
            @Param("userId")Long userId,
            @Param("date")LocalDate date
    );




}
