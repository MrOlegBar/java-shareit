package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(@Qualifier("UserServiceImpl") UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/users")
    public UserDto postUser(@Valid @RequestBody User user) {
        User userForDto = userService.create(user);
        return userMapper.toDto(userForDto);
    }

    @GetMapping(value = { "/users", "/users/{userId}"})
    public Object getUserS(@PathVariable(required = false) Long userId) throws UserNotFoundException {
        if (userId == null) {
            return userService.getAllUsers().stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            User userForDto = userService.getUserById(userId);
            return userMapper.toDto(userForDto);
        }
    }

    @PatchMapping("/users/{userId}")
    public UserDto putUser(@PathVariable Long userId,
                           @Valid @RequestBody UserDto userDto) throws UserNotFoundException {
        User userFromDto = userMapper.toUser(userDto);
        userFromDto.setId(userId);

        User userForDto = userService.update(userFromDto);
        return userMapper.toDto(userForDto);
    }

    @DeleteMapping("/users/{userId}")
    public Boolean deleteUser(@PathVariable Long userId) throws UserNotFoundException {
        return userService.deleteUser(userId);
    }
}