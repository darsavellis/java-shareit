package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {
    final UserService userService;
    final EntityManager entityManager;

    UserDto firstUser;
    UserDto secondUser;

    @BeforeEach
    void setUp() {
        firstUser = userService.getUserById(1);
        secondUser = userService.getUserById(2);
    }

    @Test
    void getUsers_thenUserListReturned() {
        List<UserDto> userDtos = List.of(firstUser, secondUser);

        List<UserDto> actualUserDtos = userService.getUsers();

        assertThat(userDtos).usingRecursiveComparison().isEqualTo(actualUserDtos);
    }

    @Test
    void getUserById_whenUserIsExist_thenUserReturned() {
        UserDto actualUserDto = userService.getUserById(firstUser.getId());

        assertThat(firstUser).usingRecursiveComparison().isEqualTo(actualUserDto);
    }

    @Test
    void createUser_whenUserIsValid_thenUserSaved() {
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.id = :id", User.class);
        query.setParameter("id", firstUser.getId());
        User actualUserInDatabase = query.getSingleResult();

        assertThat(firstUser).usingRecursiveComparison().isEqualTo(actualUserInDatabase);
    }

    @Test
    void updateUser_whenUserIsExist_thenUserUpdated() {
        UserDto newUser = UserDto.builder()
            .id(firstUser.getId())
            .name("Updated user")
            .email("updated@email.com")
            .build();

        UserDto actualUserDto = userService.updateUser(newUser);

        assertThat(newUser).usingRecursiveComparison().isEqualTo(actualUserDto);
    }

    @Test
    void updateUser_whenUserNameAndEmailIsEmpty_thenUserNameAndEmailNotUpdated() {
        UserDto newUser = UserDto.builder()
            .id(firstUser.getId())
            .name("")
            .email("")
            .build();

        UserDto actualUserDto = userService.updateUser(newUser);

        assertThat(firstUser).usingRecursiveComparison().isEqualTo(actualUserDto);

    }

    @Test
    void updateUser_whenUserNameAndEmailIsNull_thenUserNameAndEmailNotUpdated() {
        UserDto newUser = UserDto.builder()
            .id(firstUser.getId())
            .name(null)
            .email(null)
            .build();

        UserDto actualUserDto = userService.updateUser(newUser);

        assertThat(firstUser).usingRecursiveComparison().isEqualTo(actualUserDto);
    }

    @Test
    void deleteUser_whenUserIsExist_thenUserDeleted() {
        userService.deleteUser(firstUser.getId());

        assertThrows(NotFoundException.class, () -> userService.getUserById(firstUser.getId()));
    }
}
