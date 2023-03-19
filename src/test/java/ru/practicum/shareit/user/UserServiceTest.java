package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
class UserServiceTest {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
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

    @ParameterizedTest
    @ValueSource(longs = {99L, 1L})
    public void shouldReturnUserById(long userId) {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        if (userId == 99L) {
            assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        } else {
            User actual = userService.getUserById(userId);
            assertEquals(user, actual);
        }
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
        Boolean actual = userService.deleteUser(1L);

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(anyLong());

        assertEquals(expected, actual);
    }
}