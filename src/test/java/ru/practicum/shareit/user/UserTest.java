package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserTest {
    private final User userOne = new User(
            1L,
            "user@user.com",
            "user");
    private final User userTwo = new User(
            1L,
            "user@user.com",
            "user");

    @Test
    void equalsT() {
        assertFalse(userOne.equals(null));
        assertTrue(userOne.equals(userOne));
        assertTrue(userOne.equals(userTwo));
    }

    @Test
    void hashCodeT() {
        long expected = userOne.getId();
        long actual = userOne.hashCode();

        assertEquals(expected, actual);
    }
}