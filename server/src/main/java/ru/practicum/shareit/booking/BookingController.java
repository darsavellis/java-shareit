package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BookingController {
    static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    final BookingService bookingService;

    @PostMapping
    public ResponseBookingDto createBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                            @RequestBody RequestBookingDto requestBookingDto) {
        return bookingService.createBooking(userId, requestBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto reviewBooking(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long bookingId,
                                            @RequestParam boolean approved) {
        return bookingService.reviewBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getBookingById(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<ResponseBookingDto> getBookingsByBooker(@RequestHeader(USER_ID_HEADER) long userId,
                                                        @RequestParam(required = false, defaultValue = "ALL")
                                                        BookingState state) {
        return bookingService.getBookingsByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getBookingsByOwner(@RequestHeader(USER_ID_HEADER) long userId,
                                                       @RequestParam(required = false, defaultValue = "ALL")
                                                       BookingState state) {
        return bookingService.getBookingByOwner(userId, state);
    }
}
