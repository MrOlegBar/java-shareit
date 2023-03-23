package ru.practicum.shareit.gateway.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.gateway.constraintGroup.Patch;
import ru.practicum.shareit.gateway.constraintGroup.Post;

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