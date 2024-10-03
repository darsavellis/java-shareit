package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    long id;
    String description;
    UserDto requestor;
    LocalDateTime created = LocalDateTime.now();
}
