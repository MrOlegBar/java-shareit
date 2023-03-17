package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserDtoTest {
    private final UserDto userDto = UserDto.builder().build();
    private final UserDto userDtoExpected = new UserDto(1L, "user@user.com", "user");
    private final User user = new User();
    private final User userExpected = new User(
            1L,
            "user@user.com",
            "user");

    @Test
    void whenConvertUserEntityToUserDto_thenCorrect() {
        user.setId(1L);
        user.setEmail("user@user.com");
        user.setName("user");

        UserDto userDtoActual = UserMapper.toUserDto(user);

        assertEquals(userDtoExpected.getId(), userDtoActual.getId());
        assertEquals(userDtoExpected.getEmail(), userDtoActual.getEmail());
        assertEquals(userDtoExpected.getName(), userDtoActual.getName());
    }

    @Test
    void whenConvertUserDtoToUserEntity_thenCorrect() {
        userDto.setId(1L);
        userDto.setEmail("user@user.com");
        userDto.setName("user");

        User userActual = UserMapper.toUser(userDto);

        assertEquals(userExpected.getId(), userActual.getId());
        assertEquals(userExpected.getEmail(), userActual.getEmail());
        assertEquals(userExpected.getName(), userActual.getName());
    }
}
