package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User getUserById(long id);

    User createUser(User user);

    User updateUser(long id, User user);

    User deleteUser(long id);
}
