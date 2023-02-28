package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.constraintGroup.Post;
import ru.practicum.shareit.constraintGroup.Put;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    @Email(message = "Email не соответствует формату электронной почты.", groups = {Post.class,
            Put.class})
    @NotNull(message = "Электронная почта отсутствует.", groups = Post.class)
    private String email;
}