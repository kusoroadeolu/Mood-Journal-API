package com.victor.moodjournal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    private Role role;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    @ToString.Exclude
    private List<MoodEntry> moodEntries;

    public User(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
