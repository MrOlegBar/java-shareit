package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CommentDtoTest {
    private final User user = new User(
            1L,
            "user@user.com",
            "user");
    private final LocalDateTime dateTime = LocalDateTime.now();
    private final CommentDto commentDto = CommentDto.builder().build();
    private final CommentDto commentDtoExpected = new CommentDto(
            1L,
            "Add comment from user1",
            "user",
            dateTime);
    private final Comment comment = new Comment();
    private final Comment commentExpected = new Comment();

    @Test
    void whenConvertCommentEntityToCommentDto_thenCorrect() {
        comment.setId(1L);
        comment.setText("Add comment from user1");
        comment.setAuthor(user);
        comment.setItem(null);
        comment.setCreated(dateTime);

        CommentDto commentDtoActual = CommentMapper.toCommentDto(comment);

        assertEquals(commentDtoExpected.getId(), commentDtoActual.getId());
        assertEquals(commentDtoExpected.getText(), commentDtoActual.getText());
        assertEquals(commentDtoExpected.getAuthorName(), commentDtoActual.getAuthorName());
        assertEquals(commentDtoExpected.getCreated(), commentDtoActual.getCreated());
    }

    @Test
    void whenConvertCommentDtoToCommentEntity_thenCorrect() {
        commentExpected.setId(1L);
        commentExpected.setText("Add comment from user1");
        commentExpected.setAuthor(user);
        commentExpected.setCreated(dateTime);

        commentDto.setId(1L);
        commentDto.setText("Add comment from user1");
        commentDto.setAuthorName(user.getName());
        commentDto.setCreated(dateTime);

        Comment commentActual = CommentMapper.toComment(commentDto);
        commentActual.setAuthor(user);

        assertEquals(commentExpected.getId(), commentActual.getId());
        assertEquals(commentExpected.getText(), commentActual.getText());
        assertEquals(commentExpected.getAuthor(), commentActual.getAuthor());
        assertEquals(commentExpected.getItem(), commentActual.getItem());
        assertEquals(commentExpected.getCreated(), commentActual.getCreated());
    }
}
