package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.constraintGroup.Post;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
public class LessShortItemDto {
    private Long id;
    @NotBlank(message = "Название вещи отсутствует или представлено пустым символом.", groups = Post.class)
    private String name;
    @NotNull(message = "Описание вещи отсутствует.", groups = Post.class)
    private String description;
    @NotNull(message = "Статус доступности вещи отсутствует.", groups = Post.class)
    private Boolean available;
    private Long requestId;

    @Override
    public String toString() {
        return "LessShortItemDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                ", requestId=" + requestId +
                '}';
    }
}