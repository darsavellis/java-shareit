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
import java.util.Objects;

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
        User user = UserMapper.mapToUser(userDto);

        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        User oldUser = UserMapper.mapToUser(getUserById(userDto.getId()));

        if (Objects.nonNull(user.getName())) {
            oldUser.setName(user.getName());
        }
        if (Objects.nonNull(user.getEmail())) {
            oldUser.setEmail(user.getEmail());
        }

        return UserMapper.mapToUserDto(userRepository.update(oldUser));
    }

    @Override
    public UserDto deleteUser(long id) {
        return UserMapper.mapToUserDto(userRepository.delete(id));
    }
}
