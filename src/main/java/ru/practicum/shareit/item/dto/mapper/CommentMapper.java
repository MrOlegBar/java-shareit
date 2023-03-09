package ru.practicum.shareit.item.dto.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentDto;

@Component
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        return comment;
    }
}