package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.constraintGroup.Post;
import ru.practicum.shareit.constraintGroup.Put;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;
    @Email(message = "Email не соответствует формату электронной почты.", groups = {Post.class, Put.class})
    @NotNull(message = "Электронная почта отсутствует.", groups = Post.class)
    private String email;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}