package ru.practicum.shareit.user.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.DuplicatedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryUserRepository implements UserRepository {
    final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User save(User user) {
        checkIfEmailUnique(user);
        user.setId(nextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(long id, User user) {
        User oldUser = findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("User ID=%s not found", user.getEmail())));

        checkIfEmailUnique(user);
        oldUser.setId(id);
        oldUser.setName(user.getName());
        oldUser.setEmail(user.getEmail());
        return oldUser;
    }

    @Override
    public User delete(long id) {
        return users.remove(id);
    }

    long nextId() {
        long currentMaxId = users.values().stream().mapToLong(User::getId).max().orElse(0);
        return ++currentMaxId;
    }

    void checkIfEmailUnique(User user) {
        boolean isEmailUnique = users.values().stream()
            .map(User::getEmail).filter(Objects::nonNull).noneMatch(s -> s.equals(user.getEmail()));
        if (isEmailUnique) {
            user.setEmail(user.getEmail());
        } else {
            throw new DuplicatedException(String.format("Email=%s is already exists", user.getEmail()));
        }
    }
}
