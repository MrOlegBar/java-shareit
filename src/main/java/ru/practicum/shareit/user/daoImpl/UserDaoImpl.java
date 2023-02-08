package ru.practicum.shareit.user.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.UserDao;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.ValidationException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository("UserDaoImpl")
@Slf4j
public class UserDaoImpl implements UserDao {
    private final Set<User> users = new HashSet<>();

    @Override
    public User save(User user) {
        checkForUniqueness(user.getEmail());

        user.setId(getId());
        users.add(user);

        log.info("Создан пользователь: {}.", user);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users;
    }

    @Override
    public User findByUserId(long userId) throws UserNotFoundException {
        return users.stream()
                .filter(savedUser -> savedUser.getId() == userId)
                .findFirst().orElseThrow(() -> {
                    log.debug("Пользователь с userId = {} не найден.", userId);
                    throw new UserNotFoundException(String.format("Пользователь с userId = %s не найден.",
                            userId));
                });
    }

    @Override
    public User update(User user) throws UserNotFoundException {
        checkForUniqueness(user.getEmail());

        return users.stream()
                .filter(savedUser -> savedUser.getId().equals(user.getId()))
                .peek(savedUser -> {
                    savedUser.setName(user.getName() != null ? user.getName() : savedUser.getName());
                    savedUser.setEmail(user.getEmail() != null ? user.getEmail() : savedUser.getEmail());
                })
                .findFirst().orElseThrow(() -> {
                    log.debug("Пользователь с userId = {} не найден.", user.getId());
                    throw new UserNotFoundException(String.format("Пользователь с userId = %s не найден.",
                            user.getId()));
                });
    }

    @Override
    public Boolean deleteUser(long userId) throws UserNotFoundException {
        User user = findByUserId(userId);
        return users.remove(user);
    }

    private long getId() {
        return ++User.ids;
    }

    private void checkForUniqueness(String email) throws ValidationException {
        users.forEach(savedUser -> {
            if (savedUser.getEmail().equals(email)) {
                log.debug("Пользователь с email: {} уже существует.", email);
                throw new ValidationException(String.format("Пользователь с email: %s уже существует.",
                        email));
            }
        });
    }
}