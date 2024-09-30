package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(UserMapper::mapToUserDto).toList();
    }

    @Override
    public UserDto getUserById(long id) {
        return userRepository.findById(id)
            .map(UserMapper::mapToUserDto)
            .orElseThrow(() -> new NotFoundException(String.format("User ID=%s not found", id)));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.mapToUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        final User oldUser = userRepository.findById(userDto.getId())
            .orElseThrow(() -> new NotFoundException(String.format("User ID=%s not found", userDto.getId())));
        final String name = userDto.getName();
        final String email = userDto.getEmail();

        if (name != null && !name.isBlank()) {
            oldUser.setName(name);
        }
        if (email != null && !email.isBlank()) {
            oldUser.setEmail(email);
        }

        return UserMapper.mapToUserDto(userRepository.save(oldUser));
    }

    @Override
    public UserDto deleteUser(long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("User ID = %s not found", id)));
        userRepository.delete(user);
        return UserMapper.mapToUserDto(user);
    }
}
