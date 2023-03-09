package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.constraintGroup.Post;
import ru.practicum.shareit.item.dto.LessShortItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Collection;

@Builder
@Getter
@Setter
public class RequestDto {
    private Long id;
    @NotBlank(message = "Описание запроса отсутствует или представлено пустым символом.", groups = Post.class)
    private String description;
    private LocalDateTime created;
    private Collection<LessShortItemDto> items;
}