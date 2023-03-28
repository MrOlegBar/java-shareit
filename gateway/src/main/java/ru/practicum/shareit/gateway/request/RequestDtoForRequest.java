package ru.practicum.shareit.gateway.request;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.gateway.constraintGroup.Post;

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