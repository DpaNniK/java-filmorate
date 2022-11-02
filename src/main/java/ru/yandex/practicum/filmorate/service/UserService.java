package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.RequestErrorForUser;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService implements UserStorage {
    private int id = 1;
    InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
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
        return inMemoryUserStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        if (inMemoryUserStorage.users.containsKey(user.getId())) {
            if (user.getName() == null) {
                user.setName(user.getLogin());
                log.info("Пользователь не указал имени, присвоено значение логина - {}", user.getName());
            }
            log.info("Информация о пользователь {} обновлена", user.getName());
            return inMemoryUserStorage.updateUser(user);
        } else {
            log.warn("Не удалось найти пользователя {} для обновления информации", user.getName());
            throw new ValidationException(HttpStatus.NOT_FOUND
                    , "Ошибка обновления информации о пользователе");
        }
    }

    @Override
    public Collection<User> getUsers() {
        log.info("Получен GET запрос списка пользователей");
        return inMemoryUserStorage.getUsers();
    }

    @Override
    public User getUsersById(Integer id) {
        if (inMemoryUserStorage.users.containsKey(id)) {
            log.info("Получен GET запрос пользователя с id = {}", id);
            return inMemoryUserStorage.getUsersById(id);
        } else {
            log.warn("Получен GET запрос пользователя с несуществующим id - {}", id);
            throw new RequestErrorForUser(HttpStatus.NOT_FOUND
                    , "Ошибка. Пользователь с id = " + id + " не найден.");
        }
    }

    @Override
    public void addFriend(int userId, int friendId) {
        checkOnContainsInUserList(userId, friendId);

        if (inMemoryUserStorage.users.get(userId).getFriendList().contains(friendId)) {
            log.warn("Не удалось добавить пользователя {} ", inMemoryUserStorage.users.get(friendId).getName() +
                    " в друзья, так как он уже друг");
            throw new RequestErrorForUser(HttpStatus.BAD_REQUEST
                    , "Ошибка при добавлении пользователя в друзья");
        } else {
            log.info("Пользователи {} и {} подружились", inMemoryUserStorage.users.get(friendId).getName()
                    , inMemoryUserStorage.users.get(userId).getName());
            inMemoryUserStorage.addFriend(userId, friendId);
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        checkOnContainsInUserList(userId, friendId);

        if (inMemoryUserStorage.users.get(userId).getFriendList().contains(friendId)) {
            log.info("Пользователи {} и {} перестали быть друзьями"
                    , inMemoryUserStorage.users.get(friendId).getName()
                    , inMemoryUserStorage.users.get(userId).getName());
            inMemoryUserStorage.deleteFriend(userId, friendId);
        } else {
            log.warn("Не удалось найти пользователя {} в друзьях у {}"
                    , inMemoryUserStorage.users.get(friendId).getName()
                    , inMemoryUserStorage.users.get(userId).getName());
            throw new RequestErrorForUser(HttpStatus.BAD_REQUEST
                    , "Ошибка при удалении пользователя из друзей - пользователи не являются друзьями");
        }
    }

    @Override
    public Collection<User> getFriendList(int userId) {
        if (inMemoryUserStorage.users.containsKey(userId)) {
            log.info("Получен GET запрос списка друзей пользователя");
            return inMemoryUserStorage.getFriendList(userId);
        } else {
            throw new RequestErrorForUser(HttpStatus.BAD_REQUEST
                    , "Ошибка при получение друзей пользователя. Пользователя с таким id не найдено");
        }
    }

    @Override
    public Collection<User> getMutualFriendsList(int userId, int otherId) {
        checkOnContainsInUserList(userId, otherId);
        return inMemoryUserStorage.getMutualFriendsList(userId, otherId);
    }

    private void checkOnContainsInUserList(int userId, int friendId) {
        if (userId < 0 || friendId < 0) {
            log.warn("Пользователь ввел отрицательное значение id в запросе");
            throw new RequestErrorForUser(HttpStatus.NOT_FOUND
                    , "Ошибка. Введеное некорректное значение id");
        }
        if (!inMemoryUserStorage.users.containsKey(userId)) {
            log.warn("Не удалось найти пользователя с id = {} ", userId);
            throw new RequestErrorForUser(HttpStatus.NOT_FOUND
                    , "Ошибка при удалении пользователя из друзей - пользователь не найден");
        }
        if (!inMemoryUserStorage.users.containsKey(friendId)) {
            log.warn("Не удалось найти пользователя с id {} ", friendId);
            throw new RequestErrorForUser(HttpStatus.NOT_FOUND
                    , "Ошибка при удалении пользователя из друзей - пользователь не найден");
        }
    }
}

