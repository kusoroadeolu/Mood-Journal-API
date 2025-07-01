package com.victor.moodjournal.dto;

import java.time.LocalDateTime;

public record ApiError(int status, String message, String className , String thrownAt) {
}
