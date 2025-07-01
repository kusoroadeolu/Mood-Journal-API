package com.victor.moodjournal.mapper;

import com.victor.moodjournal.dto.UserDto;
import com.victor.moodjournal.dto.UserResponseDto;
import com.victor.moodjournal.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public User toUser(UserDto userDto){
        return new User(
                userDto.username(),
                userDto.email(),
                userDto.password()
        );
    }

    public UserResponseDto toUserResponseDto(User user){
        return new UserResponseDto(user.getUsername());
    }
}
