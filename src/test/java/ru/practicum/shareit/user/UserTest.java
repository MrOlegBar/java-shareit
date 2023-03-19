package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserTest {
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
        assertNotEquals(null, userOne);
        assertEquals(userOne, userOne);
        assertEquals(userOne, userTwo);
    }

    @Test
    void hashCodeT() {
        long expected = userOne.getId();
        long actual = userOne.hashCode();

        assertEquals(expected, actual);
    }
}