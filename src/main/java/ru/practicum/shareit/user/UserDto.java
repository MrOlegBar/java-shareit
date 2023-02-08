package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    @Email(message = "Email не соответствует формату электронной почты.")
    private String email;
}