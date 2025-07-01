package com.victor.moodjournal.repository;


import com.victor.moodjournal.model.UserMood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMoodRepository extends JpaRepository<UserMood, Long> {

}
