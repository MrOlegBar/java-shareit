package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    private final Long userId = 1L;
    private final Long wrongUserId = 99L;
    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .email("user@user.com")
            .name("user")
            .build();
    private final User user = UserMapper.toUser(userDto);

    @Test
    public void shouldReturnCreatedUser() {
        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(user);

        User actual = userService.create(user);

        assertEquals(user, actual);
    }

    @Test
    public void shouldReturnUpdatedUser() {
        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(user);

        User actual = userService.update(user);

        assertEquals(user, actual);
    }

    @Test
    public void shouldReturnUserById() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        User actual = userService.getUserById(userId);

        assertEquals(user, actual);
    }

    @Test
    public void shouldReturnUserNotFoundException() {
        Mockito.when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.getUserById(wrongUserId));

        String expected = String.format("Пользователь с userId = %s не найден.",
                wrongUserId);
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnAllUsers() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user));

        Collection<User> expected = List.of(user);
        Collection<User> actual = userService.getAllUsers();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnTrueAfterDeleting() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        Boolean expected = true;
        Boolean actual = userService.deleteUser(userId);

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(anyLong());

        assertEquals(expected, actual);
    }
}