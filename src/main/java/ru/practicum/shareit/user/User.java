package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;
    private String name;
    @NotNull(message = "Электронная почта отсутствует.")
    @Email(message = "Email не соответствует формату электронной почты.")
    private String email;

    private Set<Long> itemsId;
    private Set<Long> reviewId;

    public static Long ids = 0L;
}