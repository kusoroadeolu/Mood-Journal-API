package com.victor.moodjournal.dto;

import com.victor.moodjournal.model.Mood;
import com.victor.moodjournal.model.UserMood;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record MoodEntryDto(
        UserMood userMood,
        @NotBlank(message = "Note is required")
        @Size(min = 10, max = 150, message = "Minimum number of characters is 10. Maximum number of characters is 150")
        String note,
        @NotEmpty(message = "Tag is required")
        @Size(min = 3, max = 10, message = "Minimum number of characters is 3. Maximum number of characters is 10")
        String tag

) {

}
