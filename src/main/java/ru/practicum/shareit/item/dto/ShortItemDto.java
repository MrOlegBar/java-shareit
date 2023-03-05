package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.ResponseBookingForItemDto;
import ru.practicum.shareit.constraintGroup.Post;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ShortItemDto {
    private Long id;
    @NotBlank(message = "Название вещи отсутствует или представлено пустым символом.", groups = Post.class)
    private String name;
    @NotNull(message = "Описание вещи отсутствует.", groups = Post.class)
    private String description;
    @NotNull(message = "Статус доступности вещи отсутствует.", groups = Post.class)
    private Boolean available;
    private ResponseBookingForItemDto lastBooking;
    private ResponseBookingForItemDto nextBooking;
}