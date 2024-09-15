package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentItemDto {
    long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    List<ResponseCommentDto> comments = new ArrayList<>();
    LocalDateTime nextBooking;
    LocalDateTime lastBooking;
}
