package com.victor.moodjournal.dto;

import com.victor.moodjournal.model.Mood;

public record UserMoodDto(Mood mood, String emoji) {
}
