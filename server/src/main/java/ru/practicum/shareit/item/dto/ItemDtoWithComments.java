package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoWithComments {
    long id;
    String name;
    String description;
    Boolean available;
    List<ResponseCommentDto> comments = new ArrayList<>();
    Long requestId;
    LocalDateTime nextBooking;
    LocalDateTime lastBooking;
}
