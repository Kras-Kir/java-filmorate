package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserService.class})
class FilmorateApplicationTests {

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Очистка базы перед каждым тестом
        userStorage.getAllUsers().forEach(user -> userStorage.deleteUser(user.getId()));
    }

    @Test
    void contextLoads() {
        assertNotNull(userStorage);
        assertNotNull(userService);
    }

    @Test
    void testCreateUser() {
        User user = User.builder()
                .email("test@mail.com")
                .login("testLogin")
                .build();

        User created = userService.createUser(user);
        assertNotNull(created.getId());
        assertEquals(user.getEmail(), created.getEmail());
    }
}
