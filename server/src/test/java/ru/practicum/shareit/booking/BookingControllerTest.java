package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    final ObjectMapper mapper;
    @MockBean
    final BookingService bookingService;
    final MockMvc mvc;
    final long userId = 1L;
    final long bookingId = 1L;
    RequestBookingDto requestBookingDto;
    ResponseBookingDto responseBookingDto;
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");

    @BeforeEach
    void setUp() {
        UserDto userDto = UserDto.builder()
            .id(userId)
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
            .id(itemId)
            .name("ItemDto name")
            .description("ItemDto description")
            .available(true)
            .requestId(userDto.getId())
            .build();

        requestBookingDto = RequestBookingDto.builder()
            .start(LocalDateTime.parse("2024-09-15T12:00:00"))
            .end(LocalDateTime.parse("2024-09-20T12:00:00"))
            .itemId(itemDto.getId())
            .build();

        responseBookingDto = ResponseBookingDto.builder()
            .id(bookingId)
            .item(itemDto)
            .booker(userDto)
            .status(BookingStatus.WAITING)
            .start(requestBookingDto.getStart())
            .end(requestBookingDto.getEnd())
            .build();
    }

    @Test
    @SneakyThrows
    void createBooking() {
        when(bookingService.createBooking(anyLong(), any(RequestBookingDto.class))).thenReturn(responseBookingDto);

        mvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", userId)
                .content(mapper.writeValueAsString(requestBookingDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("id", notNullValue(), Long.class))
            .andExpect(jsonPath("status", equalTo(BookingStatus.WAITING.name()), BookingStatus.class))
            .andExpect(jsonPath("start", equalTo(requestBookingDto.getStart().format(dateTimeFormatter))))
            .andExpect(jsonPath("end", equalTo(requestBookingDto.getEnd().format(dateTimeFormatter))));
    }

    @Test
    @SneakyThrows
    void reviewBooking() {
        when(bookingService.reviewBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(responseBookingDto);

        mvc.perform(patch("/bookings/" + bookingId)
                .header("X-Sharer-User-Id", userId)
                .param("approved", "true"))
            .andExpect(jsonPath("id", notNullValue(), Long.class))
            .andExpect(jsonPath("status", equalTo(BookingState.WAITING.name()), String.class))
            .andExpect(jsonPath("start", equalTo(responseBookingDto.getStart().format(dateTimeFormatter))))
            .andExpect(jsonPath("end", equalTo(responseBookingDto.getEnd().format(dateTimeFormatter))));
    }

    @Test
    @SneakyThrows
    void getBookingById() {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(responseBookingDto);

        mvc.perform(get("/bookings/" + bookingId)
                .header("X-Sharer-User-Id", userId))
            .andExpect(jsonPath("id", notNullValue(), Long.class))
            .andExpect(jsonPath("status", equalTo(BookingState.WAITING.name()), String.class))
            .andExpect(jsonPath("start", equalTo(responseBookingDto.getStart().format(dateTimeFormatter))))
            .andExpect(jsonPath("end", equalTo(responseBookingDto.getEnd().format(dateTimeFormatter))));
    }

    @Test
    @SneakyThrows
    void getBookingsByBooker() {
        when(bookingService.getBookingsByBooker(anyLong(), any(BookingState.class)))
            .thenReturn(List.of(responseBookingDto));

        String response = mvc.perform(get("/bookings")
                .param("state", "ALL")
                .header("X-Sharer-User-Id", userId))
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(response, equalTo(mapper.writeValueAsString(List.of(responseBookingDto))));
    }

    @Test
    @SneakyThrows
    void getBookingsByOwner() {
        when(bookingService.getBookingByOwner(anyLong(), any(BookingState.class)))
            .thenReturn(List.of(responseBookingDto));

        String response = mvc.perform(get("/bookings/owner")
                .param("state", "ALL")
                .header("X-Sharer-User-Id", userId))
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(response, equalTo(mapper.writeValueAsString(List.of(responseBookingDto))));
    }
}
