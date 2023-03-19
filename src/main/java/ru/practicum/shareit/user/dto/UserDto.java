package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.constraintGroup.Post;
import ru.practicum.shareit.constraintGroup.Patch;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private Long id;
    @Email(message = "Email не соответствует формату электронной почты.", groups = {Post.class, Patch.class})
    @NotNull(message = "Электронная почта отсутствует.", groups = Post.class)
    private String email;
    private String name;
}