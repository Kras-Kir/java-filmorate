package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
public class FilmorateApplicationTests {

    @Autowired
    private UserDbStorage userStorage;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userStorage);
    }

    private User createTestUser() {
        return User.builder()
                .email("test@mail.ru")
                .login("testLogin")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    void testFindUserById_ShouldReturnUserWhenExists() {
        User expectedUser = userService.createUser(createTestUser());
        User actualUser = userService.getUserById(expectedUser.getId());
        assertThat(actualUser)
                .isNotNull()
                .isEqualTo(expectedUser);
    }

    @Test
    void testFindUserById_ShouldThrowWhenNotExists() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с ID 999 не найден");
    }

    @Test
    void testCreateUser_ShouldSetLoginAsNameWhenNameIsEmpty() {
        User newUser = createTestUser();
        newUser.setName("");
        User createdUser = userService.createUser(newUser);
        assertThat(createdUser.getName()).isEqualTo(newUser.getLogin());
    }

    @Test
    void testUpdateUser_ShouldUpdateExistingUser() {

        User originalUser = userService.createUser(createTestUser());
        User updatedUser = User.builder()
                .id(originalUser.getId())
                .email("updated@mail.ru")
                .login("updatedLogin")
                .name("Updated Name")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();


        User result = userService.updateUser(updatedUser);
        assertThat(result)
                .isEqualTo(updatedUser);

        User fromDb = userService.getUserById(originalUser.getId());
        assertThat(fromDb).isEqualTo(updatedUser);
    }

    @Test
    void testGetAllUsers_ShouldReturnAllCreatedUsers() {
        User user1 = userService.createUser(createTestUser());
        User user2 = userService.createUser(createTestUser());
        Collection<User> users = userService.getAllUsers();

        assertEquals(2, users.size(), "Неверное количество пользователей");
        assertTrue(users.contains(user1), "Пользователь 1 не найден");
        assertTrue(users.contains(user2), "Пользователь 2 не найден");
    }

    @Test
    void testAddFriend_ShouldAddFriendSuccessfully() {
        User user1 = userService.createUser(createTestUser());
        User user2 = userService.createUser(createTestUser());
        userService.addFriend(user1.getId(), user2.getId());
        List<User> friends = userService.getFriends(user1.getId());

        assertEquals(1, friends.size(), "Должен быть ровно один друг");

        assertEquals(user2, friends.get(0), "В списке друзей должен быть user2");

        assertTrue(friends.contains(user2), "Список друзей должен содержать user2");
    }

    @Test
    void testRemoveFriend_ShouldRemoveFriendSuccessfully() {
        User user1 = userService.createUser(createTestUser());
        User user2 = userService.createUser(createTestUser());
        Long user1Id = user1.getId();
        Long user2Id = user2.getId();
        userService.addFriend(user1Id, user2Id);

        assertEquals(1, userService.getFriends(user1Id).size(),
                "Перед удалением должен быть 1 друг");

        userService.removeFriend(user1Id, user2Id);

        List<User> friendsAfterRemoval = userService.getFriends(user1Id);
        assertTrue(friendsAfterRemoval.isEmpty(),
                "После удаления список друзей должен быть пуст");
    }

    @Test
    void testGetCommonFriends_ShouldReturnCommonFriends() {
        User user1 = userService.createUser(createTestUser());
        User user2 = userService.createUser(createTestUser());
        User commonFriend = userService.createUser(createTestUser());

        Long user1Id = user1.getId();
        Long user2Id = user2.getId();
        Long commonFriendId = commonFriend.getId();

        userService.addFriend(user1Id, commonFriendId);
        userService.addFriend(user2Id, commonFriendId);

        List<User> commonFriends = userService.getCommonFriends(user1Id, user2Id);

        assertEquals(1, commonFriends.size(),
                "Должен быть ровно один общий друг");

        assertEquals(commonFriendId, commonFriends.get(0).getId(),
                "ID общего друга должен соответствовать");
    }
}
