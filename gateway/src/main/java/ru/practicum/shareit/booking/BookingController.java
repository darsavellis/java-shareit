package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.RequestBookingDto;


@Slf4j
@Validated
@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookingByBooker(@RequestHeader(USER_ID_HEADER) long userId,
                                                     @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        BookingState state = BookingState.from(stateParam)
            .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId = {}", stateParam, userId);
        return bookingClient.getBookingByBooker(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingByOwner(@RequestHeader(USER_ID_HEADER) long userId,
                                                    @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        BookingState state = BookingState.from(stateParam)
            .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId = {} by owner", stateParam, userId);
        return bookingClient.getBookingsByOwner(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                                @RequestBody @Valid RequestBookingDto requestBookingDto) {
        log.info("Creating booking {}, userId = {}", requestBookingDto, userId);
        return bookingClient.bookItem(userId, requestBookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(USER_ID_HEADER) long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Get booking {}, userId = {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> reviewBooking(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long bookingId,
                                                @RequestParam boolean approved) {
        log.info("Review booking {}, userId = {}", bookingId, userId);
        return bookingClient.reviewBooking(userId, bookingId, approved);
    }
}
