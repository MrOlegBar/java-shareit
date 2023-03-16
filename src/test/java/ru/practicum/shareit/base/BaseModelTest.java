/*
package ru.practicum.shareit.base;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class BaseModelTest {
    User user = new User(1L, "user@user.com", "user");
    User user2 = new User(2L, "user@user.com", "user");

    @Test
    public void getIdTest() {
        Long actual = user.getId();

        assertNotNull(actual);
        assertEquals(1L, actual);
    }

    @Test
    public void setIdTest() {
        user.setId(2L);
        Long actual = user.getId();

        assertNotNull(actual);
        assertEquals(2L, actual);
    }

    @Test
    public void toStringTest() {
        String actual = user.toString();

        assertNotNull(actual);
        assertEquals("User{id='1', email='user@user.com', name=user}", actual);
    }

    @Test
    public void equalsTest() {
        Boolean actual = user.equals(user2);

        assertNotNull(actual);
        assertEquals(false, actual);
    }

    @Test
    public void hashCodeTest() {
        int actual = user.hashCode();

        assertNotNull(actual);
        assertEquals(1, actual);
    }
}
*/
