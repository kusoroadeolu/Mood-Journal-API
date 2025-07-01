package com.victor.moodjournal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;


@Entity
@Table(
        name = "mood_entry"
//        uniqueConstraints = {
//                @UniqueConstraint(columnNames = {"user_id", "created_date"})
//        }
)
@Data
@NoArgsConstructor
public class MoodEntry {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_mood_id")
    private UserMood userMood;

    @Column(length = 150)
    private String note;

    @Column(length = 20)
    private String tag;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "created_date", updatable = false)
    private LocalDate createdDate = LocalDate.now();

    @Column(insertable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public MoodEntry(UserMood userMood, String note, String tag, User user){
        this.userMood = userMood;
        this.note = note;
        this.tag = tag;
        this.user =  user;
    }

    public String formatCreatedAt(){
        DateTimeFormatter dateTimeFormatter = ofPattern("yy-MM-dd");
        return dateTimeFormatter.format(this.createdAt);
    }

}
