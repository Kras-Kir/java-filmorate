package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;


import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Получен запрос POST /users - {}", user);
        validateUser(user);
        processUserName(user);
        User createdUser = userService.createUser(user);
        log.info("Создан новый пользователь: {}", createdUser);
        return createdUser;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Получен запрос PUT /users - {}", user);
        validateUser(user);
        processUserName(user);
        User updatedUser = userService.updateUser(user);
        log.info("Обновлен пользователь: {}", updatedUser);
        return updatedUser;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получен запрос GET /users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        log.info("Получен запрос GET /users/{}", id);
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Получен запрос PUT /users/{}/friends/{}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Получен запрос DELETE /users/{}/friends/{}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        log.info("Получен запрос GET /users/{}/friends", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Получен запрос GET /users/{}/friends/common/{}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    private void processUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя пустое, используем логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            String errorMsg = "Электронная почта не может быть пустой и должна содержать @";
            log.warn(errorMsg + " - {}", user);
            throw new ValidationException(errorMsg);
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            String errorMsg = "Логин не может быть пустым и содержать пробелы";
            log.warn(errorMsg + " - {}", user);
            throw new ValidationException(errorMsg);
        }
        if (user.getBirthday() == null) {
            String errorMsg = "Дата рождения обязательна";
            log.warn("Ошибка валидации пользователя: {}", errorMsg);
            throw new ValidationException(errorMsg);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            String errorMsg = "Дата рождения не может быть в будущем";
            log.warn("Ошибка валидации пользователя: {}", errorMsg);
            throw new ValidationException(errorMsg);
        }
    }
}