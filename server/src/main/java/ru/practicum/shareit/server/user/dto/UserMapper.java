package ru.practicum.shareit.server.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.user.User;

@Component
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        return user;
    }

    public static ShortUserDto toShortUserDto(User user) {
        return ShortUserDto.builder()
                .id(user.getId())
                .build();
    }
}