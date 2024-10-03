package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestBookingDtoTest {
    final JacksonTester<RequestBookingDto> json;

    @Test
    @SneakyThrows
    void testRequestBookingDto() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");
        RequestBookingDto requestBookingDto = RequestBookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.parse("2024-09-15T12:00"))
            .end(LocalDateTime.parse("2024-09-20T12:00"))
            .build();

        JsonContent<RequestBookingDto> content = json.write(requestBookingDto);

        assertThat(content).extractingJsonPathNumberValue("$.itemId")
            .isEqualTo(requestBookingDto.getItemId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.start")
            .isEqualTo(requestBookingDto.getStart().format(dateTimeFormatter));
    }
}
