package ru.practicum.shareit.user;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
public class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;
    @MockBean
    private UserRepository userRepository;
    User user = new User();
    User testUser = new User(1L, "user@user.com", "user");
    List<User> testUsers = new ArrayList<>();

    @Before
    public void setUp() {
        user.setEmail("user@user.com");
        user.setName("user");

        testUsers.add(user);
    }

    @Test
    public void shouldReturnCreatedUser() {
        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(testUser);

        User foundUser = userService.create(user);

        assertNotNull(foundUser);
        assertEquals(testUser, foundUser);
    }

    @Test
    public void shouldReturnUpdatedUser() {
        testUser.setEmail("update@user.com");
        testUser.setName("update");

        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(testUser);

        User foundUser = userService.update(user);

        assertNotNull(foundUser);
        assertEquals(testUser, foundUser);
    }

    @Test
    public void shouldReturnUserById() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testUser));

        User foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals(testUser, foundUser);
    }

    @Test
    public void shouldReturnAllUsers() {
        Mockito.when(userRepository.findAll())
                .thenReturn(testUsers);

        Collection<User> foundUsers = userService.getAllUsers();

        assertNotNull(foundUsers);
        assertEquals(testUsers, foundUsers);
    }

    @Test
    public void shouldReturnTrueAfterDeleting() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        Boolean foundTrue = userService.deleteUser(1L);

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(anyLong());

        assertNotNull(foundTrue);
        assertEquals(true, foundTrue);
    }
}
