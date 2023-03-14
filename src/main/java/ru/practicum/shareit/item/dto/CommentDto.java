package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.constraintGroup.Post;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class CommentDto {
    private Long id;
    @NotBlank(message = "Текст комментария отсутствует или представлен пустым символом.", groups = Post.class)
    private String text;
    private String authorName;
    private LocalDateTime created;

    @Override
    public String toString() {
        return "CommentDto{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", authorName='" + authorName + '\'' +
                ", created=" + created +
                '}';
    }
}