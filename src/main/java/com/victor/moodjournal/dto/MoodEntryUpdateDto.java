package com.victor.moodjournal.dto;

public record MoodEntryUpdateDto(String note, UserMoodDto userMoodDto, String tag) {

}
