package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long userId, long friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);

        if (userId == friendId) {
            throw new IllegalArgumentException("Пользователь не может добавить самого себя в друзья");
        }

        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);

        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(long userId) {
        checkUserExists(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        checkUserExists(userId);
        checkUserExists(otherUserId);

        return userStorage.getCommonFriends(userId, otherUserId);
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        checkUserExists(user.getId());
        return userStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    private void checkUserExists(long userId) {
        if (!userStorage.getUserById(userId).isPresent()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
    }
}