package ru.practicum.shareit.server.user.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String name;
}