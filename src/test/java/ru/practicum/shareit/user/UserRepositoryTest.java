package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Sql("/schema.sql")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    private final User user = new User(
            "user@user.com",
            "user");

    @Test
    public void saveUser() {
        user.setEmail("new user@user.com");
        User savedUser = userRepository.save(user);

        assertEquals(1, savedUser.getId());
        assertEquals("new user@user.com", savedUser.getEmail());
        assertEquals("user", savedUser.getName());
    }

    @Test
    @Sql("/testSaveUniqueUser.sql")
    public void saveUniqueUser() {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () ->
                userRepository.save(user));
    }
}