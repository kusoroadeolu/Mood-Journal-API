package com.victor.moodjournal.service.impl;

import com.victor.moodjournal.dto.MoodEntryDto;
import com.victor.moodjournal.dto.MoodEntryResponseDto;
import com.victor.moodjournal.dto.MoodEntryUpdateDto;
import com.victor.moodjournal.dto.MoodStatsDto;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MoodEntryServiceImpl implements MoodEntryService {

    private final MoodEntryMapper moodEntryMapper;
    private final MoodEntryRepository moodEntryRepository;
    private final MoodStatsMapper moodStatsMapper;
    private final UserRepository userRepository;
    private final UserMoodMapper userMoodMapper;

    //Create a new mood entry. Also ensures not more than one mood entry is created per day
    //TODO Remove the constraint for one mood entry per day
    @Override
    @Transactional
    public MoodEntry createMoodEntry(MoodEntryDto moodEntryDto, Long userId) {
            User validated = validateUser(userId);

            MoodEntry moodEntry = moodEntryMapper.toMoodEntry(moodEntryDto, validated);

            if(moodEntry != null) {
                //Assign the emoji for the usermood
                String customEmoji = moodEntryDto.userMood().getUserEmoji();
                moodEntry.getUserMood().assignEmoji(customEmoji);
                return moodEntryRepository.save(moodEntry);
            }
            else
                throw new MoodEntryNotFoundException("Failed to create mood entry");

    }


    /**
     * @param userId The id of the user
     * @param moodEntryId The id of the mood entry
     * @param updateDto The dto used to update a mood entry
     * @apiNote Updates a mood entry
     * */
    @Override
    @Transactional
    public MoodEntryResponseDto updateMoodEntry(Long userId, Long moodEntryId, MoodEntryUpdateDto updateDto){
        User validated = validateUser(userId);

        MoodEntry moodEntry = moodEntryRepository.findByIdAndUserId(userId, moodEntryId)
                                                    .orElseThrow(() -> new MoodEntryNotFoundException("Could not find Mood Entry with id: " + moodEntryId));

        //Check if the moodEntry was created that day before updating
        if(!moodEntry.getCreatedDate().equals(LocalDate.now())){
            throw new MoodEntryCreationException("Cannot update this mood entry, because it was not created today.");
        }

        //Map the update dto to the user mood
        UserMood userMood = userMoodMapper.toUserMood(updateDto.userMoodDto());
        userMood.assignEmoji(updateDto.userMoodDto().emoji());

        //Check if the user mood and note of the update dto are the same as the mood entry before updating
        if(userMood.getUserMood() != moodEntry.getUserMood().getUserMood()){
            moodEntry.setUserMood(userMood);
        }

        if(!Objects.equals(updateDto.note(), moodEntry.getNote())){
            moodEntry.setNote(updateDto.note());
        }

        moodEntry.setTag(updateDto.tag());
        moodEntry.setUpdatedAt(LocalDateTime.now());

        var savedMoodEntry = moodEntryRepository.save(moodEntry);
        return moodEntryMapper.toMoodEntryResponse(savedMoodEntry);
    }

    /**
     * @param id The id of the mood entry
     * @apiNote Deletes a mood entry
     * */
    @Override
    public void deleteMoodEntry(Long id){
        MoodEntry moodEntry = moodEntryRepository.findById(id)
                                        .orElseThrow(() -> new MoodEntryNotFoundException("Could not find Mood Entry with id: " + id));
        moodEntryRepository.delete(moodEntry);
    }

    /**
     * @param userId The id of the user
     * @param start The start date to filter from
     * @param end The end date to stop filtering at
     * @param page
     * @param size The number of entries that should be loaded
     * @apiNote Gets mood entries by user, start and end date.
     * */
    @Override
    public Page<MoodEntryResponseDto> findMoodEntriesByUser(Long userId, LocalDate start, LocalDate end, int page, int size){
        User validated = validateUser(userId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<MoodEntry> moodEntries;

        if(start != null && end != null){
            moodEntries = moodEntryRepository.findByUserIdAndCreatedDateBetween(userId, start, end, pageable);

        } else{
            moodEntries = moodEntryRepository.findByUserId(userId, pageable);
        }

        List<MoodEntryResponseDto> moodEntryResponseDtos = moodEntries.stream()
                                                                .map(this.moodEntryMapper::toMoodEntryResponse)
                                                                .toList();
        return new PageImpl<>(moodEntryResponseDtos);
    }

    /**
     * @param userId The id of the user
     * @param date The date to find mood entries at
     * @apiNote Finds a mood entry at a particular date
     * */
    @Override
    public MoodEntryResponseDto findMoodEntryByUserAndCreatedDate(Long userId, LocalDate date) {
        User validated = validateUser(userId);

        MoodEntry moodEntry = moodEntryRepository.findByUserIdAndCreatedDate(userId, date).
                orElseThrow(() -> new MoodEntryNotFoundException("Could not find a mood entry created at date: " + date));

        return moodEntryMapper.toMoodEntryResponse(moodEntry);
    }

    /**
     * @param id The id of the mood entry
     * @apiNote Finds a mood entry by id
     * */
    @Override
    public MoodEntryResponseDto findMoodEntryById(Long id) {
        var moodEntry = moodEntryRepository.findById(id)
                .orElseThrow(() -> new MoodEntryNotFoundException("Could not find Mood Entry with id: " + id));
        return moodEntryMapper.toMoodEntryResponse(moodEntry);
    }

    @Override
    public MoodStatsDto getMoodStatsBetween(Long userId, LocalDate start, LocalDate end) {
        User validated = validateUser(userId);

        List<MoodEntry> moodEntries = moodEntryRepository.findByUserIdAndCreatedDateBetween(userId, start, end);
        Map<Mood, Integer> moodCount = new HashMap<>();
        int averageWeight = 0;

        for(var moodEntry: moodEntries){
            //Map each mood to the number of times they occurred
            Mood mood = moodEntry.getUserMood().getUserMood();
            moodCount.put(mood, moodCount.getOrDefault(mood, 0) + 1);

            //Get the average mood
            int weight = moodEntry.getUserMood().getUserMood().getWeight();
            averageWeight += weight;
        }

        Mood frequentMood = null;
        int maxCount = 0;

        //Get the most frequent mood
        for(Map.Entry<Mood, Integer> entry: moodCount.entrySet()){
            if(entry.getValue() > maxCount){
                frequentMood = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        Mood averageMood = UserMood.fromWeight(averageWeight);
        long numberOfEntries = moodEntryRepository.countByUserIdAndCreatedDateBetween(userId, start, end);

        return moodStatsMapper.mapMoodStats(frequentMood, averageMood, numberOfEntries);
    }

    @Override
    public Page<MoodEntryResponseDto> findMoodEntriesByUserAndMood(Long userId, Mood mood, int page, int size) {
            User validated = validateUser(userId);

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<MoodEntry> moodEntries = moodEntryRepository.findByUserIdAndUserMood(userId, mood, pageable);

            List<MoodEntryResponseDto> moodEntryResponseDtos =
                                    moodEntries.stream().map(this.moodEntryMapper::toMoodEntryResponse).toList();
            return new PageImpl<>(moodEntryResponseDtos);
    }

    public User validateUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id:" + userId + "not found"));
    }
}
