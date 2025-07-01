package com.victor.moodjournal.mapper;

import com.victor.moodjournal.dto.UserDto;
import com.victor.moodjournal.dto.UserResponseDto;
import com.victor.moodjournal.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    @Test
    public void should_map_user_dto_to_user(){
        //given
        UserDto userDto = new UserDto(
                "test_name",
                "test_email",
                "test_password"
        );

        //when
        User user = userMapper.toUser(userDto);

        assertNotNull(user);
        assertEquals(userDto.username(), user.getUsername());
        assertEquals(userDto.email(), user.getEmail());
        assertEquals(userDto.password(), user.getPassword());

    }

    @Test
    public void should_map_user_to_response_dto(){
        //given
        User user = User.builder()
                .username("test_name")
                .email("test_email")
                .password("test_password")
                .build();

        //when
        UserResponseDto responseDto = userMapper.toUserResponseDto(user);

        //then
        assertNotNull(responseDto);
        assertEquals(user.getUsername(), responseDto.username());
    }

}