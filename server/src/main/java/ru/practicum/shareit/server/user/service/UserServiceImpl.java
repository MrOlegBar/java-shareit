package ru.practicum.shareit.server.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.user.UserNotFoundException;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.User;

import javax.transaction.Transactional;
import java.util.Collection;

@Service("UserServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(long userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.debug("Пользователь с userId = {} не найден.", userId);
            throw new UserNotFoundException(String.format("Пользователь с userId = %s не найден.",
                    userId));
        });
    }

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public Boolean deleteUser(long userId) throws UserNotFoundException {
        userRepository.deleteById(userId);
        return !userRepository.existsById(userId);
    }
}
