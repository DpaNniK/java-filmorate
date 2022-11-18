package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.mapper.UserIdMapper;
import ru.yandex.practicum.filmorate.storage.dao.mapper.UserMapper;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component("dbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Возвращаю именно юзера из-за тестов в постмане
    @Override
    public User createUser(User user) {
        jdbcTemplate.update("INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY)" +
                " VALUES (?, ?, ?, ?) ", user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        return getUsersById(user.getId());
    }

    //Здесь аналогичная ситуация
    @Override
    public User updateUser(User user) {
        jdbcTemplate.update("UPDATE USERS SET EMAIL=?, LOGIN=?, NAME=?, BIRTHDAY=? WHERE USER_ID=?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        return getUsersById(user.getId());
    }

    @Override
    public Collection<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM USERS", new UserMapper());
    }

    @Override
    public User getUsersById(Integer id) {
        return jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE USER_ID = ?", new UserMapper(), id);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        getUsersById(userId).getFriendList().add(friendId);
        //Проверка, что в ответ на запрос дружбы пришло подтверждение, в случае true во friend_list добавляется
        //новая пара друзей, а статус дружбы меняется на подтвержденный
        //Проверка нужна для того, чтобы лишь сменить статус дружбы на подтвержденный,
        //а также добавить новую запись с уже подтвержденным статусом дружбы
        if (checkOnContainsInFriendList(friendId, userId)) {
            jdbcTemplate.update("INSERT INTO USERS_FRIENDSHIP (USER_ID, FRIEND_ID, FRIENDSHIP_STATUS) " +
                    "VALUES (?, ?, ? ) ", userId, friendId, 2);
            jdbcTemplate.update("UPDATE USERS_FRIENDSHIP SET FRIENDSHIP_STATUS =? " +
                    "WHERE USER_ID = ? AND FRIEND_ID = ?", friendId, userId, 2);
        } else {
            jdbcTemplate.update("INSERT INTO USERS_FRIENDSHIP (USER_ID, FRIEND_ID, FRIENDSHIP_STATUS) " +
                    "VALUES (?, ?, ? ) ", userId, friendId, 1);
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        jdbcTemplate.update("DELETE FROM USERS_FRIENDSHIP WHERE USER_ID=? AND FRIEND_ID =?",
                userId, friendId);
    }

    @Override
    public Collection<User> getFriendList(int userId) {
        List<User> friendsList = new ArrayList<>();
        List<Integer> friendId = jdbcTemplate.query("SELECT FRIEND_ID FROM USERS_FRIENDSHIP WHERE USER_ID=?",
                new UserIdMapper(), userId);
        for (Integer id : friendId) {
            friendsList.add(getUsersById(id));
        }
        return friendsList;
    }

    @Override
    public Collection<User> getMutualFriendsList(int userId, int otherId) {
        List<User> mutualFriendsList = new ArrayList<>();
        for (User user : getFriendList(userId)) {
            for (User otherUser : getFriendList(otherId)) {
                if (user.getId().equals(otherUser.getId())) {
                    mutualFriendsList.add(user);
                }
            }
        }
        return mutualFriendsList;
    }

    private boolean checkOnContainsInFriendList(int userId, int friendId) {
        for (User user : getFriendList(userId)) {
            if (user.getId() == friendId) return true;
        }
        return false;
    }
}