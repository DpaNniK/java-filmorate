package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    Collection<User> getUsers();

    User getUsersById(Integer id);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    Collection<User> getFriendList(int userId);

    Collection<User> getMutualFriendsList(int userId, int otherId);
}
