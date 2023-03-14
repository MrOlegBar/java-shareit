package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.constraintGroup.Post;
import ru.practicum.shareit.constraintGroup.Put;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@ToString
public class UserDto {
    private Long id;
    @Email(message = "Email не соответствует формату электронной почты.", groups = {Post.class,
            Put.class})
    @NotNull(message = "Электронная почта отсутствует.", groups = Post.class)
    private String email;
    private String name;
}