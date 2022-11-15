package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.RequestErrorForUser;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService implements UserStorage {
    private int id = 1;

    final UserStorage userStorage;

    public UserService(@Qualifier("dbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User createUser(User user) {
        if (user.getId() == null) {
            user.setId(id);
            log.info("Пользователю присвоен id = {} автоматически", user.getId());
            id++;
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Пользователь не указал имени, присвоено значение логина - {}", user.getName());
        }
        log.info("Пользователь {} добавлен в список", user.getName());
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        if (checkOnContainsUser(user.getId())) {
            if (user.getName() == null) {
                user.setName(user.getLogin());
                log.info("Пользователь не указал имени, присвоено значение логина - {}", user.getName());
            }
            log.info("Информация о пользователь {} обновлена", user.getName());
            return userStorage.updateUser(user);
        } else {
            log.warn("Не удалось найти пользователя {} для обновления информации", user.getName());
            throw new ValidationException(HttpStatus.NOT_FOUND
                    , "Ошибка обновления информации о пользователе");
        }
    }

    @Override
    public Collection<User> getUsers() {
        log.info("Получен GET запрос списка пользователей");
        return userStorage.getUsers();
    }

    @Override
    public User getUsersById(Integer id) {
        if (checkOnContainsUser(id)) {
            log.info("Получен GET запрос пользователя с id = {}", id);
            return userStorage.getUsersById(id);
        } else {
            log.warn("Получен GET запрос пользователя с несуществующим id - {}", id);
            throw new RequestErrorForUser(HttpStatus.NOT_FOUND
                    , "Ошибка. Пользователь с id = " + id + " не найден.");
        }
    }

    @Override
    public void addFriend(int userId, int friendId) {
        checkOnContainsInUserList(userId, friendId);

        if (userStorage.getUsersById(userId).getFriendList().contains(friendId)) {
            log.warn("Не удалось добавить пользователя {} ", userStorage.getUsersById(friendId).getName() +
                    " в друзья, так как он уже друг");
            throw new RequestErrorForUser(HttpStatus.BAD_REQUEST
                    , "Ошибка при добавлении пользователя в друзья");
        } else {
            log.info("Пользователи {} и {} подружились", userStorage.getUsersById(friendId).getName()
                    , userStorage.getUsersById(userId).getName());
            userStorage.addFriend(userId, friendId);
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        checkOnContainsInUserList(userId, friendId);

        if (checkOnContainsInFriendList(userId, friendId)) {
            log.info("Пользователи {} и {} перестали быть друзьями"
                    , userStorage.getUsersById(friendId).getName()
                    , userStorage.getUsersById(userId).getName());
            userStorage.deleteFriend(userId, friendId);
        } else {
            log.warn("Не удалось найти пользователя {} в друзьях у {}"
                    , userStorage.getUsersById(friendId).getName()
                    , userStorage.getUsersById(userId).getName());
            throw new RequestErrorForUser(HttpStatus.BAD_REQUEST
                    , "Ошибка при удалении пользователя из друзей - пользователи не являются друзьями");
        }
    }

    @Override
    public Collection<User> getFriendList(int userId) {
        if (checkOnContainsUser(userId)) {
            log.info("Получен GET запрос списка друзей пользователя");
            return userStorage.getFriendList(userId);
        } else {
            throw new RequestErrorForUser(HttpStatus.BAD_REQUEST
                    , "Ошибка при получение друзей пользователя. Пользователя с таким id не найдено");
        }
    }

    @Override
    public Collection<User> getMutualFriendsList(int userId, int otherId) {
        checkOnContainsInUserList(userId, otherId);
        return userStorage.getMutualFriendsList(userId, otherId);
    }

    private void checkOnContainsInUserList(int userId, int friendId) {
        if (userId < 0 || friendId < 0) {
            log.warn("Пользователь ввел отрицательное значение id в запросе");
            throw new RequestErrorForUser(HttpStatus.NOT_FOUND
                    , "Ошибка. Введеное некорректное значение id");
        }
        if (!checkOnContainsUser(userId)) {
            log.warn("Не удалось найти пользователя с id = {} ", userId);
            throw new RequestErrorForUser(HttpStatus.NOT_FOUND
                    , "Ошибка при удалении пользователя из друзей - пользователь не найден");
        }
        if (!checkOnContainsUser(friendId)) {
            log.warn("Не удалось найти пользователя с id {} ", friendId);
            throw new RequestErrorForUser(HttpStatus.NOT_FOUND
                    , "Ошибка при удалении пользователя из друзей - пользователь не найден");
        }
    }

    private boolean checkOnContainsUser(int userId) {
        for (User user : userStorage.getUsers()) {
            if (user.getId() == userId) return true;
        }
        return false;
    }

    private boolean checkOnContainsInFriendList(int userId, int friendId) {
        for (User user : userStorage.getFriendList(userId)) {
            if (user.getId() == friendId) return true;
        }
        return false;
    }
}

