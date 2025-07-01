package com.victor.moodjournal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Comparator;


@Data
@Entity
@NoArgsConstructor
public class UserMood {
    @Id
    @GeneratedValue
    @Column(name = "user_mood_id")
    private Long id;

    @NotNull(message = "Mood type is required")
    @Enumerated(EnumType.STRING)
    private Mood userMood;
    private String userEmoji;

    public UserMood(Mood mood, String emoji){
        this.userMood = mood;
        if(userMood != Mood.CUSTOM){
            this.userEmoji = this.userMood.getEmoji();
        }else{
            this.userEmoji = emoji;
        }
    }

    public void assignEmoji(String customEmoji){
        if(this.userMood != Mood.CUSTOM){
            this.userEmoji = this.userMood.getEmoji();
        }else{
            if(this.userEmoji.trim().isEmpty()){
                throw new IllegalArgumentException("Custom Emoji cannot be null or empty");
            }
            this.userEmoji = customEmoji;
        }
    }

    public String displayEmoji(String userEmoji){
        return (this.userMood == Mood.CUSTOM) ? userEmoji : userMood.getEmoji();
    }

    public boolean isCustom(){
        return userMood == Mood.CUSTOM;
    }


    //Pass the average mood weight
    public static Mood fromWeight(int weight){
        Mood closest = null;
        int minDifference = Integer.MAX_VALUE;

        for(Mood mood : Mood.values()){
            if(mood == Mood.CUSTOM)continue;

            int diff = Math.abs(mood.getWeight() - weight);
            if(diff < minDifference){
                minDifference = diff;
                closest = mood;
            }
        }
        return closest != null ? closest : Mood.CUSTOM;
    }

    @Override
    public String toString() {
        return ("Mood: " + this.userEmoji + " " + getUserEmoji());
    }
}
