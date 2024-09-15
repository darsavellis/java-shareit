package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    List<Booking> findAllByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwnerId(Long ownerId);

    List<Booking> findAllByItemId(Long itemId);

    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findAllByItemOwnerAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerAndStartIsAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemOwnerAndStatus(Long ownerId, BookingStatus status, Sort sort);

    Boolean existsByBookerIdAndItemIdAndEndBefore(Long ownerId, Long itemId, LocalDateTime end);
}
