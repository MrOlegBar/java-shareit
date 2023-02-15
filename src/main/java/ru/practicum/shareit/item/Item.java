package ru.practicum.shareit.item;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Builder
public class Item {
    private Long id;
    @NotBlank(message = "Название вещи отсутствует или представлено пустым символом.")
    private String name;
    @NotNull(message = "Описание вещи отсутствует.")
    private String description;
    @NotNull(message = "Статус доступности вещи отсутствует.")
    private Boolean available;
    private Long userId;
    private Long request;
    private Set<Long> reviewsId;

    public static Long ids = 0L;
}