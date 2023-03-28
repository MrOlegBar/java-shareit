package ru.practicum.shareit.gateway.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.gateway.constraintGroup.Post;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class LessShortItemDto {
    private Long id;
    @NotBlank(message = "Название вещи отсутствует или представлено пустым символом.", groups = Post.class)
    private String name;
    @NotNull(message = "Описание вещи отсутствует.", groups = Post.class)
    private String description;
    @NotNull(message = "Статус доступности вещи отсутствует.", groups = Post.class)
    private Boolean available;
    private Long requestId;
}