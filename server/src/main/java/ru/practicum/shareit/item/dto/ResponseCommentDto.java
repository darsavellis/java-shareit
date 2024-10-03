package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseCommentDto {
    Long id;
    String text;
    String authorName;
    LocalDateTime created;
}
