package ru.practicum.shareit.user.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.util.Collection;

@Service("UserServiceImpl")
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
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
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Boolean deleteUser(long userId) throws UserNotFoundException {
        getUserById(userId);

        userRepository.deleteById(userId);
        return !userRepository.existsById(userId);
    }
}
