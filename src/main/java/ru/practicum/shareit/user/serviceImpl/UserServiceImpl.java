package ru.practicum.shareit.user.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;

@Service("UserServiceImpl")
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(@Qualifier("UserDaoImpl") UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User create(User user) {
        return userDao.save(user);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public User getUserById(long userId) throws UserNotFoundException {
        return userDao.findByUserId(userId);
    }

    @Override
    public User update(User user) throws UserNotFoundException {
        return userDao.update(user);
    }

    @Override
    public Boolean deleteUser(long userId) throws UserNotFoundException {
        return userDao.deleteUser(userId);
    }
}
