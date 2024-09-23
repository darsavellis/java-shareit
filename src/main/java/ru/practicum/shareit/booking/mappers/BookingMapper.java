package ru.practicum.shareit.booking.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.user.mappers.UserMapper;

@UtilityClass
public class BookingMapper {
    public Booking mapToBooking(RequestBookingDto requestBookingDto) {
        Booking booking = new Booking();
        booking.setStart(requestBookingDto.getStart());
        booking.setEnd(requestBookingDto.getEnd());
        return booking;
    }

    public ResponseBookingDto mapToResponseBookingDto(Booking booking) {
        ResponseBookingDto responseBookingDto = new ResponseBookingDto();
        responseBookingDto.setId(booking.getId());
        responseBookingDto.setStart(booking.getStart());
        responseBookingDto.setEnd(booking.getEnd());
        responseBookingDto.setItem(ItemMapper.mapToItemDto(booking.getItem()));
        responseBookingDto.setBooker(UserMapper.mapToUserDto(booking.getBooker()));
        responseBookingDto.setStatus(booking.getStatus());
        return responseBookingDto;
    }
}
