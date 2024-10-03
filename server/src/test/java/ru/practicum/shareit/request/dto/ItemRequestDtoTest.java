package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestDtoTest {
    final JacksonTester<ItemRequestDto> json;

    @Test
    @SneakyThrows
    void testItemRequestDto() {
        LocalDateTime created = LocalDateTime.now();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("ItemRequest description")
            .created(created)
            .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) itemRequestDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
    }
}
