package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.PermissionException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.model.BookingStatus.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {
    final BookingRepository bookingRepository;
    final UserRepository userRepository;
    final ItemRepository itemRepository;

    @Override
    public ResponseBookingDto createBooking(long userId, RequestBookingDto requestBookingDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User ID=%s not found", userId)));

        long itemId = requestBookingDto.getItemId();
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException(String.format("Item ID=%s not found", itemId)));

        if (!item.isAvailable()) {
            throw new NotAvailableException(String.format("Item ID=%s not available", item.getId()));
        }

        Booking booking = BookingMapper.mapToBooking(requestBookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(WAITING);

        return BookingMapper.mapToResponseBookingDto(bookingRepository.save(booking));
    }

    @Override
    public ResponseBookingDto reviewBooking(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException(String.format("Booking ID=%s not found", bookingId)));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new PermissionException("Access denied");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.mapToResponseBookingDto(booking);
    }

    @Override
    public ResponseBookingDto getBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException(String.format("Booking ID=%s not found", bookingId)));

        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new PermissionException("Access denied");
        }

        return BookingMapper.mapToResponseBookingDto(booking);
    }

    @Override
    public List<ResponseBookingDto> getBookingsByBooker(long userId, BookingState state) {
        final Sort sort = Sort.by(Sort.Direction.ASC, "start");
        final LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBookerId(userId, sort);
            case PAST -> bookingRepository.findAllByBookerIdAndEndIsBefore(userId, now, sort);
            case FUTURE -> bookingRepository.findAllByBookerIdAndStartIsAfter(userId, now, sort);
            case WAITING -> bookingRepository.findAllByBookerIdAndStatus(userId, WAITING, sort);
            case CURRENT -> bookingRepository.findAllByBookerIdAndStatus(userId, APPROVED, sort);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatus(userId, REJECTED, sort);
        };

        return bookings.stream().map(BookingMapper::mapToResponseBookingDto).toList();
    }

    @Override
    public List<ResponseBookingDto> getBookingByOwner(long userId, BookingState state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User ID=%s does not exists", userId));
        }

        final Sort sort = Sort.by(Sort.Direction.ASC, "start");
        final LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByItemOwnerId(userId, sort);
            case PAST -> bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, now, sort);
            case FUTURE -> bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, now, sort);
            case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatus(userId, WAITING, sort);
            case CURRENT -> bookingRepository.findAllByItemOwnerIdAndStatus(userId, APPROVED, sort);
            case REJECTED -> bookingRepository.findAllByItemOwnerIdAndStatus(userId, REJECTED, sort);
        };

        return bookings.stream().map(BookingMapper::mapToResponseBookingDto).toList();
    }
}
