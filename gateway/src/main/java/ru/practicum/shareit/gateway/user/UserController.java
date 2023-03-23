package ru.practicum.shareit.gateway.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.constraintGroup.Patch;
import ru.practicum.shareit.gateway.constraintGroup.Post;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping("/users")
    public ResponseEntity<Object> postUser(@Validated({Post.class}) @RequestBody UserDto userDto) {
        return userClient.postUser(userDto);
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<Object> patchUser(@PathVariable Long userId,
                                            @Validated({Patch.class}) @RequestBody UserDto userDto) {
        return userClient.patchUser(userId, userDto);
    }

    @GetMapping(value = {"/users", "/users/{userId}"})
    public ResponseEntity<Object> getUserS(@PathVariable(required = false) Long userId) {
        if (userId == null) {
            return userClient.getUsers();
        } else {
            return userClient.getUser(userId);
        }
    }

    @DeleteMapping(value = "/users/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userClient.deleteUser(userId);
    }
}