package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    ResponseBookingDto createBooking(long userId, RequestBookingDto requestBookingDto);

    ResponseBookingDto reviewBooking(long userId, long bookingId, boolean approved);

    ResponseBookingDto getBookingById(long userId, long bookingId);

    List<ResponseBookingDto> getBookingsByBooker(long userId, BookingState state);

    List<ResponseBookingDto> getBookingByOwner(long userId, BookingState state);
}
