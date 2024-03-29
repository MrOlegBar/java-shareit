package ru.practicum.shareit.server.user.service;

import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserNotFoundException;

import java.util.Collection;

public interface UserService {
    User create(User user);

    Collection<User> getAllUsers();

    User getUserByIdOrElseThrow(long userId) throws UserNotFoundException;

    User update(User user) throws UserNotFoundException;

    Boolean deleteUser(long userId) throws UserNotFoundException;
}