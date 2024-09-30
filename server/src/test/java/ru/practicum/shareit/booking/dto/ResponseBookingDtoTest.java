package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ResponseBookingDtoTest {
    final JacksonTester<ResponseBookingDto> json;

    @Test
    @SneakyThrows
    void testResponseBookingDto() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");

        UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("ItemDto name")
            .description("ItemDto description")
            .available(true)
            .requestId(userDto.getId())
            .build();

        ResponseBookingDto responseBookingDto = ResponseBookingDto.builder()
            .id(1L)
            .item(itemDto)
            .booker(userDto)
            .status(BookingStatus.WAITING)
            .start(LocalDateTime.parse("2024-09-15T12:00:00"))
            .end(LocalDateTime.parse("2024-09-20T12:00:00"))
            .build();

        JsonContent<ResponseBookingDto> content = json.write(responseBookingDto);

        assertThat(content).extractingJsonPathNumberValue("$.id")
            .isEqualTo(responseBookingDto.getId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.start")
            .isEqualTo(responseBookingDto.getStart().format(dateTimeFormatter));
        assertThat(content).extractingJsonPathStringValue("$.end")
            .isEqualTo(responseBookingDto.getEnd().format(dateTimeFormatter));
        assertThat(content).extractingJsonPathStringValue("$.status")
            .isEqualTo(BookingStatus.WAITING.name());
    }
}
