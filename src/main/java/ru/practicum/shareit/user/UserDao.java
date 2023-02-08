package ru.practicum.shareit.user;

import ru.practicum.shareit.ValidationException;

import java.util.Collection;

public interface UserDao {
    User save(User user) throws ValidationException;

    User findByUserId(long userId) throws UserNotFoundException;

    Collection<User> getAllUsers();

    User update(User user) throws UserNotFoundException;

    Boolean deleteUser(long userId) throws UserNotFoundException;
}