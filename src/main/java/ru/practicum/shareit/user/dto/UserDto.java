package ru.practicum.shareit.user.dto;

import lombok.*;
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

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (this == o) {
            return true;
        } else if (!(o instanceof UserDto)) {
            return false;
        } else {
            UserDto userDto = (UserDto) o;
            if (!userDto.getClass().equals(this.getClass())) {
                return false;
            } else {
                return this.id != null && this.id.equals(userDto.id);
            }
        }
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }
}