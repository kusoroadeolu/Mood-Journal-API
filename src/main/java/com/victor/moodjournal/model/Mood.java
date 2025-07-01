package com.victor.moodjournal.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Mood {
    HAPPY("☺️", 2),
    SAD("😥", -2),
    ANXIOUS("😰", -1),
    EXCITED("🤩", 3),
    ANGRY("😠", -3),
    CUSTOM("", 0);

    private final String emoji;
    private final int weight;

    @JsonCreator
    public static Mood fromString(String mood){
        try{
            return Mood.valueOf(mood.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Mood.CUSTOM;
        }
    }


}
