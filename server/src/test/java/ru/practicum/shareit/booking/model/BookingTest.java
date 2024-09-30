package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@SpringBootTest
class BookingTest {
    @Test
    void testBooking() {
        LocalDateTime start = LocalDateTime.parse("2024-09-15T12:00:00");
        LocalDateTime end = LocalDateTime.parse("2024-09-20T12:00:00");
        Booking booking = new Booking(1L, start, end, new Item(), new User(), BookingStatus.WAITING);

        assertThat(booking.getId(), equalTo(1L));
        assertThat(booking.getStart(), equalTo(start));
        assertThat(booking.getEnd(), equalTo(end));
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
    }
}
