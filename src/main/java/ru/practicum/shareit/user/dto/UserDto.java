package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.OnCreate;
import ru.practicum.shareit.user.OnUpdate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    long id;
    String name;
    @Email(groups = {OnCreate.class, OnUpdate.class})
    @NotNull(groups = OnCreate.class)
    String email;
}
