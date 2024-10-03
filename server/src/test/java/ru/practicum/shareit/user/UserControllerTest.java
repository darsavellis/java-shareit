package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    final ObjectMapper mapper;
    @MockBean
    final UserService userService;
    final MockMvc mvc;
    UserDto userDto;

    @BeforeEach
    void setUpUserDto() {
        userDto = UserDto.builder()
            .id(1L)
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();
    }

    @Test
    @SneakyThrows
    void getUsers() {
        when(userService.getUsers()).thenReturn(Collections.emptyList());

        mvc.perform(get("/users")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(Collections.emptyList().toString()));
    }

    @Test
    @SneakyThrows
    void createUser_whenUserValid_thenUserSaved() {
        when(userService.createUser(userDto)).thenReturn(userDto);

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
            .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));

        verify(userService, times(1)).createUser(userDto);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @SneakyThrows
    void getUserById_whenUserIdExist_thenUserReturned() {
        long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(userDto);

        mvc.perform(get("/users/" + userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
            .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
    }

    @Test
    @SneakyThrows
    void updateUser() {
        when(userService.updateUser(userDto)).thenReturn(userDto);

        mvc.perform(patch("/users/" + userDto.getId())
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
            .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
    }

    @Test
    @SneakyThrows
    void deleteUser() {
        long userId = 1L;

        when(userService.deleteUser(userId)).thenReturn(userDto);

        mvc.perform(delete("/users/" + userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
            .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
    }
}
