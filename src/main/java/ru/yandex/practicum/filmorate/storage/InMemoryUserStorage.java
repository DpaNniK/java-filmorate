package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    public Map<Integer, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User getUsersById(Integer id) {
        return users.get(id);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        users.get(userId).getFriendList().add(friendId);
        users.get(friendId).getFriendList().add(userId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        users.get(userId).getFriendList().remove(friendId);
        users.get(friendId).getFriendList().remove(userId);
    }

    @Override
    public Collection<User> getFriendList(int userId) {
        List<User> friendList = new ArrayList<>();
        for (Integer id : users.get(userId).getFriendList()) {
            friendList.add(users.get(id));
        }
        return friendList;
    }

    @Override
    public Collection<User> getMutualFriendsList(int userId, int otherId) {
        List<User> mutualFriendsList = new ArrayList<>();
        for (Integer id : users.get(userId).getFriendList()) {
            for (Integer newId : users.get(otherId).getFriendList()) {
                if (id.equals(newId)) {
                    mutualFriendsList.add(users.get(id));
                }
            }
        }
        return mutualFriendsList;
    }
}
