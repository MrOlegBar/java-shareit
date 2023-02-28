package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserService {
    User create(User user);

    Collection<User> getAllUsers();

    User getUserById(long userId) throws UserNotFoundException;

    User update(User user) throws UserNotFoundException;

    Boolean deleteUser(long userId) throws UserNotFoundException;
}