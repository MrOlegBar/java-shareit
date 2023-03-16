package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
public class UserServiceImplTest {
    @Autowired
    private UserService userServiceImpl;
    @MockBean
    private UserRepository userRepository;
    User userForRequest;
    User testUser;
    List<User> testUsers;

    @BeforeEach
    public void setUp() {
        userForRequest = new User("user@user.com", "user");

        testUser = new User(1L, "user@user.com", "user");

        testUsers = new ArrayList<>();
        testUsers.add(userForRequest);
    }

    @Test
    public void shouldReturnCreatedUser() {
        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(testUser);

        User foundUser = userServiceImpl.create(userForRequest);

        assertNotNull(foundUser);
        assertEquals(testUser, foundUser);
    }

    @Test
    public void shouldReturnUpdatedUser() {
        testUser.setEmail("update@user.com");
        testUser.setName("update");

        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(testUser);

        User foundUser = userServiceImpl.update(userForRequest);

        assertNotNull(foundUser);
        assertEquals(testUser, foundUser);
    }

    @Test
    public void shouldReturnUserById() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testUser));

        User foundUser = userServiceImpl.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals(testUser, foundUser);
    }

    @Test
    public void shouldReturnUserNotFoundException() {
        Mockito.when(userRepository.findById(99L))
                .thenThrow(new UserNotFoundException(String.format("Пользователь с userId = %s не найден.",
                        99L)));

        Exception exception = assertThrows(UserNotFoundException.class, () -> userServiceImpl.getUserById(99L));

        String expectedMessage = "Пользователь с userId = 99 не найден.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void shouldReturnAllUsers() {
        Mockito.when(userRepository.findAll())
                .thenReturn(testUsers);

        Collection<User> foundUsers = userServiceImpl.getAllUsers();

        assertNotNull(foundUsers);
        assertEquals(testUsers, foundUsers);
    }

    @Test
    public void shouldReturnTrueAfterDeleting() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        Boolean foundTrue = userServiceImpl.deleteUser(1L);

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(anyLong());

        assertNotNull(foundTrue);
        assertEquals(true, foundTrue);
    }
}