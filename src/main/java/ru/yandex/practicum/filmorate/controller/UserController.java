package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")

public class UserController {

    Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @GetMapping()
    public Collection<User> getUsers() {
        log.info("Получен GET запрос списка пользователей");
        return users.values();
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            user.setId(id);
            log.info("Пользователю присвоен id = {} автоматически", user.getId());
            id++;
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.info("Пользователь не указал имени, присвоено значение логина - {}", user.getName());
        }
        log.info("Пользователь {} добавлен в список", user.getName());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping()
    public User putUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            if (user.getId() == null) {
                user.setId(id);
                log.info("Пользователю присвоен id = {} автоматически", user.getId());
                id++;
            }
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
                log.info("Пользователь не указал имени, присвоено значение логина - {}", user.getName());
            }
            log.info("Информация о пользователь {} обновлена", user.getName());
            users.put(user.getId(), user);
        } else {
            log.warn("Не удалось найти пользователя {} для обновления информации", user.getName());
            throw new ValidationException("Ошибка обновления информации о пользователе");
        }
        return user;
    }
}
