package ru.practicum.shareit.server.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.dto.UserMapper;
import ru.practicum.shareit.server.user.service.UserServiceImpl;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping("/users")
    public UserDto postUser(@RequestBody UserDto userDto) {
        User userFromDto = UserMapper.toUser(userDto);
        User userForDto = userService.create(userFromDto);
        return UserMapper.toUserDto(userForDto);
    }

    @PatchMapping("/users/{userId}")
    public UserDto patchUser(@PathVariable Long userId,
                             @RequestBody UserDto userDto) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        User userForDto = userService.update(user);
        return UserMapper.toUserDto(userForDto);
    }

    @GetMapping(value = {"/users", "/users/{userId}"})
    public Object getUserS(@PathVariable(required = false) Long userId) throws UserNotFoundException {
        if (userId == null) {
            return userService.getAllUsers().stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            User userForDto = userService.getUserById(userId);
            return UserMapper.toUserDto(userForDto);
        }
    }

    @DeleteMapping("/users/{userId}")
    public Boolean deleteUser(@PathVariable Long userId) throws UserNotFoundException {
        userService.getUserById(userId);

        return userService.deleteUser(userId);
    }
}