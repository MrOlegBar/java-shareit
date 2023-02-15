package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public UserDto postUser(@Valid @RequestBody User user) {
        User userForDto = userService.create(user);
        return UserMapper.toDto(userForDto);
    }

    @GetMapping(value = { "/users", "/users/{userId}"})
    public Object getUserS(@PathVariable(required = false) Long userId) throws UserNotFoundException {
        if (userId == null) {
            return userService.getAllUsers().stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            User userForDto = userService.getUserById(userId);
            return UserMapper.toDto(userForDto);
        }
    }

    @PatchMapping("/users/{userId}")
    public UserDto putUser(@PathVariable Long userId,
                           @Valid @RequestBody UserDto userDto) throws UserNotFoundException {
        User userFromDto = UserMapper.toUser(userDto);
        userFromDto.setId(userId);

        User userForDto = userService.update(userFromDto);
        return UserMapper.toDto(userForDto);
    }

    @DeleteMapping("/users/{userId}")
    public Boolean deleteUser(@PathVariable Long userId) throws UserNotFoundException {
        return userService.deleteUser(userId);
    }
}