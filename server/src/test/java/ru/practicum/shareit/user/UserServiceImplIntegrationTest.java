package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {
    final UserService userService;
    final EntityManager entityManager;

    @Test
    void getUsers_thenUserListReturned() {
        UserDto firstUser = UserDto.builder()
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        UserDto secondUser = UserDto.builder()
            .name("Sergey")
            .email("sergeev@email.com")
            .build();

        firstUser = userService.createUser(firstUser);
        secondUser = userService.createUser(secondUser);

        List<UserDto> userDtos = List.of(firstUser, secondUser);

        List<UserDto> actualUserDtos = userService.getUsers();

        for (UserDto userDto : userDtos) {
            assertThat(actualUserDtos, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(userDto.getName())),
                hasProperty("email", equalTo(userDto.getEmail()))
            )));
        }
    }

    @Test
    void getUserById_whenUserIsExist_thenUserReturned() {
        UserDto user = UserDto.builder()
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        user = userService.createUser(user);

        UserDto actualUserDto = userService.getUserById(user.getId());

        assertThat(actualUserDto.getId(), equalTo(user.getId()));
        assertThat(actualUserDto.getName(), equalTo(user.getName()));
        assertThat(actualUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void createUser_whenUserIsValid_thenUserSaved() {
        UserDto userDto = UserDto.builder()
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        userDto = userService.createUser(userDto);

        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.id = :id", User.class);
        query.setParameter("id", userDto.getId());
        User actualUserInDatabase = query.getSingleResult();

        assertThat(actualUserInDatabase.getId(), equalTo(userDto.getId()));
        assertThat(actualUserInDatabase.getName(), equalTo(userDto.getName()));
        assertThat(actualUserInDatabase.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUser_whenUserIsExist_thenUserUpdated() {
        UserDto oldUser = UserDto.builder()
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        UserDto newUser = UserDto.builder()
            .name("Sergey")
            .email("sergeev@email.com")
            .build();

        oldUser = userService.createUser(oldUser);
        newUser.setId(oldUser.getId());

        UserDto updatedUserDto = userService.updateUser(newUser);

        assertThat(updatedUserDto.getId(), equalTo(newUser.getId()));
        assertThat(updatedUserDto.getName(), equalTo(newUser.getName()));
        assertThat(updatedUserDto.getEmail(), equalTo(newUser.getEmail()));
    }

    @Test
    void updateUser_whenUserNameAndEmailIsEmpty_thenUserNameAndEmailNotUpdated() {
        UserDto oldUser = UserDto.builder()
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        UserDto newUser = UserDto.builder()
            .name("")
            .email("")
            .build();

        oldUser = userService.createUser(oldUser);
        newUser.setId(oldUser.getId());

        UserDto updatedUserDto = userService.updateUser(newUser);

        assertThat(updatedUserDto.getId(), equalTo(newUser.getId()));
        assertThat(updatedUserDto.getName(), equalTo(oldUser.getName()));
        assertThat(updatedUserDto.getEmail(), equalTo(oldUser.getEmail()));
    }

    @Test
    void updateUser_whenUserNameAndEmailIsNull_thenUserNameAndEmailNotUpdated() {
        UserDto oldUser = UserDto.builder()
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        UserDto newUser = UserDto.builder()
            .name(null)
            .email(null)
            .build();

        oldUser = userService.createUser(oldUser);
        newUser.setId(oldUser.getId());

        UserDto updatedUserDto = userService.updateUser(newUser);

        assertThat(updatedUserDto.getId(), equalTo(newUser.getId()));
        assertThat(updatedUserDto.getName(), equalTo(oldUser.getName()));
        assertThat(updatedUserDto.getEmail(), equalTo(oldUser.getEmail()));
    }

    @Test
    void deleteUser_whenUserIsExist_thenUserDeleted() {
        UserDto user = UserDto.builder()
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        user = userService.createUser(user);
        UserDto userInDatabase = userService.getUserById(user.getId());
        assertThat(userInDatabase, not(Optional.empty()));

        long userId = user.getId();
        userService.deleteUser(user.getId());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }
}
