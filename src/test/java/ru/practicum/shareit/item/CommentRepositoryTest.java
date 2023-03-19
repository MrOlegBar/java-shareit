package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql("/schema.sql")
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private final Comment comment = new Comment();
    private final Item item = new Item();
    private final User user = new User(
            1L,
            "user@user.com",
            "user");
    private final LocalDateTime dateTime = LocalDateTime.now();

    @Test
    public void shouldReturnCreatedUser() {
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Простая дрель");
        item.setAvailable(true);
        item.setOwner(user);

        userRepository.save(user);
        itemRepository.save(item);

        comment.setText("Add comment from user1");
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(dateTime);

        Comment savedComment = commentRepository.save(comment);

        assertEquals(1, savedComment.getId());
        assertEquals("Add comment from user1", savedComment.getText());
        assertEquals(user, savedComment.getAuthor());
        assertEquals(item, savedComment.getItem());
        assertEquals(dateTime, savedComment.getCreated());
    }
}
