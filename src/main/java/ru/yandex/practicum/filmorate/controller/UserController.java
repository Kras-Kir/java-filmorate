package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Получен запрос POST /users - {}", user);
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя пустое, используем логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        user.setId(idCounter++);
        users.put(user.getId(), user);
        log.info("Создан новый пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Получен запрос PUT /users - {}", user);
        validateUser(user);
        if (!users.containsKey(user.getId())) {
            String errorMsg = "Пользователь с ID " + user.getId() + " не найден";
            log.error(errorMsg);
            throw new ValidationException(errorMsg);
        }
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получен запрос GET /users");
        return users.values();
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
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            String errorMsg = "Дата рождения не может быть в будущем";
            log.warn(errorMsg + " - {}", user);
            throw new ValidationException(errorMsg);
        }
    }
}