package com.victor.moodjournal.exception;

public class MoodEntryNotFoundException extends RuntimeException {
    public MoodEntryNotFoundException(String message) {
        super(message);
    }
}
