package com.victor.moodjournal.controller;

import com.victor.moodjournal.dto.MoodEntryDto;
import com.victor.moodjournal.dto.MoodEntryResponseDto;
import com.victor.moodjournal.dto.MoodEntryUpdateDto;
import com.victor.moodjournal.dto.MoodStatsDto;
import com.victor.moodjournal.mapper.MoodEntryMapper;
import com.victor.moodjournal.model.Mood;
import com.victor.moodjournal.model.MoodEntry;
import com.victor.moodjournal.model.UserPrincipal;
import com.victor.moodjournal.service.MoodEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/moods")
@RequiredArgsConstructor
public class MoodEntryController {
    private final MoodEntryService moodEntryService;
    private final MoodEntryMapper moodEntryMapper;


    /**
     * @param moodEntryDto Input dto object of a mood entry
     * */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MoodEntryResponseDto> createMoodEntry(
            @RequestBody @Valid MoodEntryDto moodEntryDto,
            @AuthenticationPrincipal UserPrincipal currentUser
            ){
        Long userId = currentUser.getId();

        //Invoke mood entry service to create a mood entry from the dto
        MoodEntry moodEntry = moodEntryService.createMoodEntry(moodEntryDto, userId);
        URI uri = URI.create("/api/v1/moods/" + moodEntry.getId());

        var moodEntryResponseDto = moodEntryMapper.toMoodEntryResponse(moodEntry);
        return ResponseEntity.created(uri).body(moodEntryResponseDto);
    }

    //Gets the mood entries of a specific user. Can be filtered by start and end date
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<MoodEntryResponseDto>> getMoodEntriesByUser(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser
    ){
        Long userId = currentUser.getId();
        Page<MoodEntryResponseDto> moodEntries = moodEntryService.findMoodEntriesByUser(userId, start, end, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(moodEntries);
    }

    //Updates a mood entry for a user
    @PutMapping("/user/{moodEntryId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MoodEntryResponseDto> updateMoodEntry(
            @PathVariable Long moodEntryId,
            @RequestBody @Valid MoodEntryUpdateDto updateDto,
            @AuthenticationPrincipal UserPrincipal currentUser
    ){
       Long userId = currentUser.getId();
       var response = moodEntryService.updateMoodEntry(userId, moodEntryId, updateDto);
       return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //Deletes a mood entry by id
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteMoodEntry(@PathVariable Long id){
        moodEntryService.deleteMoodEntry(id);
        return ResponseEntity.noContent().build();
    }

    //Gets a mood entry for a user created on a certain day
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MoodEntryResponseDto> getMoodEntryByUserAndDate(
            @RequestParam(name = "date") LocalDate date,
            @AuthenticationPrincipal UserPrincipal currentUser
    ){
        Long userId = currentUser.getId();
        MoodEntryResponseDto moodEntry = moodEntryService.findMoodEntryByUserAndCreatedDate(userId, date);
        return ResponseEntity.status(HttpStatus.OK).body(moodEntry);
    }

    @GetMapping("/entry/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MoodEntryResponseDto> getMoodEntryById(@PathVariable Long id){
        var moodEntry = moodEntryService.findMoodEntryById(id);
        return ResponseEntity.ok().body(moodEntry);
    }

    @GetMapping("/user/summary")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MoodStatsDto> getMoodSummaryByUser(
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate end,
                                                             @AuthenticationPrincipal UserPrincipal currentUser
                                                            ){
        Long userId = currentUser.getId();
        MoodStatsDto moodStatsDto = moodEntryService.getMoodStatsBetween(userId ,start, end);
        return ResponseEntity.ok().body(moodStatsDto);
    }

    @GetMapping("/user/filter")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<MoodEntryResponseDto>> getMoodEntriesByMood(
                                                       @RequestParam Mood mood,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @AuthenticationPrincipal UserPrincipal currentUser
    ){
        Long userId = currentUser.getId();
        Page<MoodEntryResponseDto> moodEntryResponseDtos = moodEntryService.findMoodEntriesByUserAndMood(userId, mood, page, size);
        return ResponseEntity.ok().body(moodEntryResponseDtos);
    }




}
