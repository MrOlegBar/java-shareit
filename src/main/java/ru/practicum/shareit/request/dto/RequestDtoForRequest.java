package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.constraintGroup.Post;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Builder
@Getter
public class RequestDtoForRequest {
    private Long id;
    @NotBlank(message = "Описание запроса отсутствует или представлено пустым символом.", groups = Post.class)
    private String description;
    private LocalDateTime created;
}