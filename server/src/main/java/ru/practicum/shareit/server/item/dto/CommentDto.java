package ru.practicum.shareit.server.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.server.constraintGroup.Post;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank(message = "Текст комментария отсутствует или представлен пустым символом.", groups = Post.class)
    private String text;
    private String authorName;
    private LocalDateTime created;
}