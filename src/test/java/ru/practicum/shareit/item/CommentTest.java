package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class CommentTest {
    private final Comment commentOne = new Comment(
            "Add comment from user1",
            null,
            null);
    private final Comment commentTwo = new Comment(
            1L,
            "Add comment from user1",
            null,
            null);

    @BeforeEach
    void setUp() {
        commentOne.setId(1L);
        commentOne.setCreated(null);
    }

    @Test
    void equalsT() {
        assertNotEquals(null, commentOne);
        assertEquals(commentOne, commentOne);
        assertEquals(commentOne, commentTwo);
    }

    @Test
    void hashCodeT() {
        long expected = commentOne.getId();
        long actual = commentOne.hashCode();

        assertEquals(expected, actual);
    }

    @Test
    void toStringT() {
        String expected = commentOne.toString();
        String actual = "Comment(id=1, text=Add comment from user1, author=null, created=null, item=null)";

        assertEquals(expected, actual);
    }
}
