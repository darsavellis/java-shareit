package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void getUsers_thenUserListReturned() {
        User firstUser = User
            .builder()
            .id(1L)
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        User secondUser = User
            .builder()
            .id(1L)
            .name("Sergey")
            .email("sergeev@email.com")
            .build();

        User thirdUser = User
            .builder()
            .id(1L)
            .name("Ivan")
            .email("ivanov@email.com")
            .build();

        UserDto firstUserDto = UserMapper.mapToUserDto(firstUser);
        UserDto secondUserDto = UserMapper.mapToUserDto(secondUser);
        UserDto thirdUserDto = UserMapper.mapToUserDto(thirdUser);

        List<User> users = List.of(firstUser, secondUser, thirdUser);
        List<UserDto> userDtos = List.of(firstUserDto, secondUserDto, thirdUserDto);

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> actualUserDtos = userService.getUsers();

        for (UserDto userDto : userDtos) {
            assertThat(actualUserDtos, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(userDto.getName())),
                hasProperty("email", equalTo(userDto.getEmail()))
            )));
        }

        verify(userRepository).findAll();
    }

    @Test
    void getUserById_whenUserExist_thenUserReturned() {
        User userToSave = User
            .builder()
            .id(1L)
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        UserDto userDtoToSave = UserMapper.mapToUserDto(userToSave);

        when(userRepository.findById(userToSave.getId())).thenReturn(Optional.of(userToSave));

        UserDto actualUserDto = userService.getUserById(userToSave.getId());

        assertThat(userDtoToSave.getId(), notNullValue());
        assertThat(userDtoToSave.getName(), equalTo(actualUserDto.getName()));
        assertThat(userDtoToSave.getEmail(), equalTo(actualUserDto.getEmail()));

        verify(userRepository).findById(userToSave.getId());
    }

    @Test
    void getUserById_whenUserNotExist_thenThrowsNotFoundException() {
        long userId = 10L;

        when(userRepository.findById(userId))
            .thenThrow(new NotFoundException(String.format("User ID=%s not found", userId)));

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void createUser_whenUserIsValid_thenUserSaved() {
        User userToSave = User
            .builder()
            .id(1L)
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        UserDto userDtoToSave = UserMapper.mapToUserDto(userToSave);

        when(userRepository.save(userToSave)).thenReturn(userToSave);

        UserDto actualUserDto = userService.createUser(userDtoToSave);

        assertThat(actualUserDto.getId(), notNullValue());
        assertThat(actualUserDto.getName(), equalTo(userDtoToSave.getName()));
        assertThat(actualUserDto.getEmail(), equalTo(userDtoToSave.getEmail()));

        verify(userRepository).save(userToSave);
    }

    @Test
    void updateUser_whenUserExist_thenUserUpdated() {
        User oldUser = User
            .builder()
            .id(1L)
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        User newUser = User
            .builder()
            .id(1L)
            .name("Sergey")
            .email("sergeev@email.com")
            .build();

        UserDto oldUserDto = UserMapper.mapToUserDto(oldUser);
        UserDto newUserDto = UserMapper.mapToUserDto(newUser);

        when(userRepository.findById(oldUserDto.getId())).thenReturn(Optional.of(oldUser));
        when(userRepository.save(ArgumentMatchers.any())).thenReturn(oldUser);

        UserDto actualUserDto = userService.updateUser(newUserDto);

        verify(userRepository).save(userArgumentCaptor.capture());

        User savedUser = userArgumentCaptor.getValue();

        assertThat(newUserDto.getId(), equalTo(savedUser.getId()));
        assertThat(newUserDto.getName(), equalTo(savedUser.getName()));
        assertThat(newUserDto.getEmail(), equalTo(savedUser.getEmail()));
    }

    @Test
    void deleteUser_whenUserExist_thenUserDeleted() {
        User user = User
            .builder()
            .id(1L)
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        UserDto userDto = UserMapper.mapToUserDto(user);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        UserDto actualUserDto = userService.deleteUser(user.getId());

        assertThat(actualUserDto.getId(), notNullValue());
        assertThat(actualUserDto.getName(), equalTo(userDto.getName()));
        assertThat(actualUserDto.getEmail(), equalTo(userDto.getEmail()));
        verify(userRepository, times(1)).delete(user);
    }

    @AfterEach
    void postSetUp() {
        userRepository.deleteAll();
    }
}
