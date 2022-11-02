package ru.yandex.practicum.filmorate.service;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FilmorateApplication.class)
public class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    InMemoryUserStorage inMemoryUserStorage;

    @AfterEach
    @Autowired
    public void afterEach() {
        this.inMemoryUserStorage = new InMemoryUserStorage();
        this.userService = new UserService(inMemoryUserStorage);
    }

    @Test
    public void correctCreateUserTest() {
        User user = new User("mail@mail.ru", "dolore", LocalDate.parse("1946-08-20"));
        user.setName("Nick Name");

        userService.createUser(user);

        assertNotNull(userService.inMemoryUserStorage.users
                , "Ошибка. Пользователь не добавился в список");
        assertEquals(userService.inMemoryUserStorage.users.size(), 1
                , "Ошибка. В хранилище неверное количество пользователей");

        User userFromStorage = userService.getUsersById(1);
        assertEquals(userFromStorage.getName(), user.getName()
                , "Имя пользователя в запросе не совпадает с именем в хранилище");
        assertEquals(userFromStorage.getLogin(), user.getLogin()
                , "Логин пользователя в запросе не совпадает с логином в хранилище");
        assertEquals(userFromStorage.getBirthday(), user.getBirthday()
                , "Дата рождения пользователя в запросе не совпадает с датой в хранилище");
        assertEquals(userFromStorage.getEmail(), user.getEmail()
                , "Почта пользователя в запросе не совпадает с почтой в хранилище");
    }

    @Test
    public void correctUpdateUserTest() {
        User user = new User("mail@mail.ru", "dolore", LocalDate.parse("1946-08-20"));
        user.setName("Nick Name");
        User newUser = new User("mail@yandex.ru", "doloreUpdate", LocalDate.parse("1976-09-20"));
        newUser.setName("New User");
        newUser.setId(1);

        userService.createUser(user);
        userService.updateUser(newUser);

        User userFromStorage = userService.getUsersById(1);
        assertEquals(userFromStorage.getName(), newUser.getName()
                , "Имя пользователя в запросе не совпадает с именем в хранилище");
        assertEquals(userFromStorage.getLogin(), newUser.getLogin()
                , "Логин пользователя в запросе не совпадает с логином в хранилище");
        assertEquals(userFromStorage.getBirthday(), newUser.getBirthday()
                , "Дата рождения пользователя в запросе не совпадает с датой в хранилище");
        assertEquals(userFromStorage.getEmail(), newUser.getEmail()
                , "Почта пользователя в запросе не совпадает с почтой в хранилище");
    }

    @Test
    public void correctAddFriendUserTest() {
        User user = new User("mail@mail.ru", "dolore", LocalDate.parse("1946-08-20"));
        user.setName("Nick Name");
        user.setId(1);
        User friendUser = new User("mail@yandex.ru", "doloreFriend", LocalDate.parse("1976-09-20"));
        friendUser.setName("Friend User");
        user.setId(2);

        userService.createUser(user);
        userService.createUser(friendUser);
        userService.addFriend(user.getId(), friendUser.getId());

        assertEquals(userService.getUsersById(user.getId()).getFriendList().size(), 1
                , "Пользователь не добавлен в список друзей");
        assertEquals(userService.getUsersById(friendUser.getId()).getFriendList().size(), 1
                , "Пользователь не добавлен в список друзей");
        assertTrue(userService.getUsersById(user.getId()).getFriendList().contains(friendUser.getId())
                , "Пользователя нет в друзьях");
        assertTrue(userService.getUsersById(friendUser.getId()).getFriendList().contains(user.getId())
                , "Пользователя нет в друзьях");
    }

    @Test
    public void correctDeleteFriendUserTest() {
        User user = new User("mail@mail.ru", "dolore", LocalDate.parse("1946-08-20"));
        user.setName("Nick Name");
        user.setId(1);
        User friendUser = new User("mail@yandex.ru", "doloreFriend", LocalDate.parse("1976-09-20"));
        friendUser.setName("Friend User");
        user.setId(2);

        userService.createUser(user);
        userService.createUser(friendUser);
        userService.addFriend(user.getId(), friendUser.getId());
        userService.deleteFriend(user.getId(), friendUser.getId());

        assertEquals(userService.getUsersById(user.getId()).getFriendList().size(), 0
                , "Пользователь не удален из друзей");
        assertEquals(userService.getUsersById(friendUser.getId()).getFriendList().size(), 0
                , "Пользователь не удален из друзей");
        assertFalse(userService.getUsersById(user.getId()).getFriendList().contains(friendUser.getId())
                , "Пользователя не удален из друзей");
        assertFalse(userService.getUsersById(friendUser.getId()).getFriendList().contains(user.getId())
                , "Пользователя не удален из друзей");
    }

    @Test
    public void get404StatusForFalseIdUser() {
        ResponseStatusException ex = Assertions.assertThrows(
                ResponseStatusException.class,
                generateExecutableForFalseUserId());
        assertEquals(ex.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void get404StatusForForAddFalseUserIdInFriend() {
        ResponseStatusException ex = Assertions.assertThrows(
                ResponseStatusException.class,
                generateExecutableForAddFalseUserIdInFriend());
        assertEquals(ex.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void get404StatusForForDeleteFalseUserIdFromFriend() {
        ResponseStatusException ex = Assertions.assertThrows(
                ResponseStatusException.class,
                generateExecutableForDeleteFalseUserIdFromFriends());
        assertEquals(ex.getStatus(), HttpStatus.NOT_FOUND);
    }

    private Executable generateExecutableForFalseUserId() {
        return () -> userService.getUsersById(1);
    }

    private Executable generateExecutableForAddFalseUserIdInFriend() {
        return () -> userService.addFriend(1, 1);
    }

    private Executable generateExecutableForDeleteFalseUserIdFromFriends() {
        return () -> userService.deleteFriend(1, 1);
    }
}
