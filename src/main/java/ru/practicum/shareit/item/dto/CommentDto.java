package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.constraintGroup.Post;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDto {
    private Long id;
    @NotBlank(message = "Текст комментария отсутствует или представлен пустым символом.", groups = Post.class)
    private String text;
    private String authorName;
    private LocalDateTime created;
}