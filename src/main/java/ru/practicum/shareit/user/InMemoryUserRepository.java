package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.DuplicatedException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryUserRepository implements UserRepository {
    static long currentMaxId;
    final Map<Long, User> users = new HashMap<>();
    final Map<String, User> emails = new HashMap<>();

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
        emails.put(user.getEmail(), user);
        return user;
    }

    @Override
    public User update(User user) {
        User oldUser = users.get(user.getId());
        oldUser.setName(user.getName());
        oldUser.setEmail(user.getEmail());
        checkIfEmailUnique(oldUser);
        emails.put(user.getEmail(), user);
        return oldUser;
    }

    @Override
    public User delete(long id) {
        return users.remove(id);
    }

    long nextId() {
        return ++currentMaxId;
    }

    void checkIfEmailUnique(User user) {
        User userWithSameEmail = emails.get(user.getEmail());
        if (userWithSameEmail != null && userWithSameEmail != user) {
            throw new DuplicatedException(String.format("Email=%s is already exists", user.getEmail()));
        }
    }
}
