package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql("/schema.sql")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    private final User user = new User(
            "user@user.com",
            "user");

    @Test
    @DirtiesContext
    public void shouldReturnCreatedUser() {
        User savedUser = userRepository.save(user);

        assertEquals(1, savedUser.getId());
        assertEquals("user@user.com", savedUser.getEmail());
        assertEquals("user", savedUser.getName());
    }
}