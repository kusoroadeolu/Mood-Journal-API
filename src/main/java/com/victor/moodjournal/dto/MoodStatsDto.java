package com.victor.moodjournal.dto;

import com.victor.moodjournal.model.Mood;

public record MoodStatsDto(Mood frequentMood, Mood averageMood, long numberOfEntries) {
}
