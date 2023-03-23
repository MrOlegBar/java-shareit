package ru.practicum.shareit.server.item.dto.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.user.User;

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
        User author = new User();
        author.setName(commentDto.getAuthorName());

        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setAuthor(author);
        if (commentDto.getCreated() != null) {
            comment.setCreated(commentDto.getCreated());
        }
        return comment;
    }
}