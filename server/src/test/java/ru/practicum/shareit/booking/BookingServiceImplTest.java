package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.PermissionException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");
    final BookingService bookingService;
    final UserService userService;
    final ItemService itemService;

    UserDto ownerDto;
    UserDto bookerDto;
    ItemDto firstItemDto;
    ItemDto secondItemDto;
    RequestBookingDto firstRequestBookingDto;
    RequestBookingDto secondRequestBookingDto;

    @BeforeEach
    void setUp() {
        ownerDto = UserDto.builder()
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        bookerDto = UserDto.builder()
            .name("Sergey")
            .email("sergeev@email.com")
            .build();

        firstItemDto = ItemDto.builder()
            .name("First itemDto name")
            .description("First itemDto description")
            .available(true)
            .build();

        secondItemDto = ItemDto.builder()
            .name("Second itemDto name")
            .description("Second itemDto description")
            .available(true)
            .build();

        firstRequestBookingDto = RequestBookingDto.builder()
            .start(LocalDateTime.parse("2024-09-15T12:00:00"))
            .end(LocalDateTime.parse("2024-09-20T12:00:00"))
            .itemId(firstItemDto.getId())
            .build();

        secondRequestBookingDto = RequestBookingDto.builder()
            .start(LocalDateTime.parse("2024-09-15T12:00:00"))
            .end(LocalDateTime.parse("2024-09-20T12:00:00"))
            .itemId(secondItemDto.getId())
            .build();
    }

    @Test
    void createBooking_whenUserAndItemExists_thenBookingSaved() {
        ownerDto = userService.createUser(ownerDto);
        firstItemDto = itemService.createItem(ownerDto.getId(), firstItemDto);
        firstRequestBookingDto.setItemId(firstItemDto.getId());
        ResponseBookingDto responseBOokingDto = bookingService.createBooking(ownerDto.getId(), firstRequestBookingDto);

        assertThat(responseBOokingDto.getId(), notNullValue());
        assertThat(responseBOokingDto.getItem(), equalTo(firstItemDto));
        assertThat(responseBOokingDto.getBooker(), equalTo(ownerDto));
        assertThat(responseBOokingDto.getStart(), equalTo(firstRequestBookingDto.getStart()));
        assertThat(responseBOokingDto.getEnd(), equalTo(firstRequestBookingDto.getEnd()));
        assertThat(responseBOokingDto.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void createBooking_whenItemIsNotAvailable_thenThrowsNotAvailableException() {
        ownerDto = userService.createUser(ownerDto);
        firstItemDto.setAvailable(false);
        firstItemDto = itemService.createItem(ownerDto.getId(), firstItemDto);
        firstRequestBookingDto.setItemId(firstItemDto.getId());

        assertThrows(NotAvailableException.class, () -> {
            bookingService.createBooking(ownerDto.getId(), firstRequestBookingDto);
        });
    }

    @Test
    void reviewBooking_whenBookingExistAndNotApproved_thenBookingApprovedAndReturned() {
        ownerDto = userService.createUser(ownerDto);
        bookerDto = userService.createUser(bookerDto);
        firstItemDto = itemService.createItem(ownerDto.getId(), firstItemDto);
        firstRequestBookingDto.setItemId(firstItemDto.getId());
        ResponseBookingDto responseBookingDto = bookingService.createBooking(bookerDto.getId(), firstRequestBookingDto);

        assertThat(responseBookingDto.getStatus(), equalTo(BookingStatus.WAITING));

        responseBookingDto = bookingService.reviewBooking(ownerDto.getId(), responseBookingDto.getId(), true);

        assertThat(responseBookingDto.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getBookingById_whenBookingExist_thenBookingReturned() {
        ownerDto = userService.createUser(ownerDto);
        bookerDto = userService.createUser(bookerDto);
        firstItemDto = itemService.createItem(ownerDto.getId(), firstItemDto);
        firstRequestBookingDto.setItemId(firstItemDto.getId());
        ResponseBookingDto responseBookingDto = bookingService.createBooking(bookerDto.getId(), firstRequestBookingDto);

        long bookingId = responseBookingDto.getId();
        ResponseBookingDto actualResponseBookingDto = bookingService.getBookingById(bookerDto.getId(), bookingId);

        assertThat(actualResponseBookingDto, equalTo(responseBookingDto));
    }

    @Test
    void getBookingById_whenUserIsNotBookerAndOwner_thenThrowsPermissionException() {
        ownerDto = userService.createUser(ownerDto);
        bookerDto = userService.createUser(bookerDto);
        firstItemDto = itemService.createItem(ownerDto.getId(), firstItemDto);
        firstRequestBookingDto.setItemId(firstItemDto.getId());
        ResponseBookingDto responseBookingDto = bookingService.createBooking(bookerDto.getId(), firstRequestBookingDto);

        assertThrows(PermissionException.class, () -> bookingService.getBookingById(100L, responseBookingDto.getId()));
    }

    @Test
    void getBookingsByOwner_whenOwnerBookingsExists_thenBookingListReturned() {
        ownerDto = userService.createUser(ownerDto);
        bookerDto = userService.createUser(bookerDto);

        firstItemDto = itemService.createItem(ownerDto.getId(), firstItemDto);
        secondItemDto = itemService.createItem(bookerDto.getId(), secondItemDto);

        firstRequestBookingDto.setItemId(firstItemDto.getId());
        secondRequestBookingDto.setItemId(secondItemDto.getId());

        ResponseBookingDto firstResponseBookingDto =
            bookingService.createBooking(bookerDto.getId(), firstRequestBookingDto);
        ResponseBookingDto secondResponseBookingDto =
            bookingService.createBooking(bookerDto.getId(), secondRequestBookingDto);

        List<ResponseBookingDto> bookingDtos = List.of(firstResponseBookingDto, secondResponseBookingDto);
        List<ResponseBookingDto> actualBookingDtos =
            bookingService.getBookingByOwner(ownerDto.getId(), BookingState.ALL);

        assertThat(actualBookingDtos, hasSize(1));
        assertThat(actualBookingDtos, hasItem(allOf(
            hasProperty("id", notNullValue()),
            hasProperty("item", equalTo(firstResponseBookingDto.getItem())),
            hasProperty("start", equalTo(firstResponseBookingDto.getStart()))
        )));

        List<ResponseBookingDto> pastBookingDtos =
            bookingService.getBookingByOwner(ownerDto.getId(), BookingState.PAST);
        List<ResponseBookingDto> currentBookingDtos =
            bookingService.getBookingByOwner(ownerDto.getId(), BookingState.CURRENT);
        List<ResponseBookingDto> futureBookingDtos =
            bookingService.getBookingByOwner(ownerDto.getId(), BookingState.FUTURE);
        List<ResponseBookingDto> rejectedBookingDtos =
            bookingService.getBookingByOwner(ownerDto.getId(), BookingState.REJECTED);
        List<ResponseBookingDto> waitingBookingDtos =
            bookingService.getBookingByOwner(ownerDto.getId(), BookingState.WAITING);

        assertThat(pastBookingDtos, hasSize(1));
        assertThat(currentBookingDtos, hasSize(0));
        assertThat(futureBookingDtos, hasSize(0));
        assertThat(rejectedBookingDtos, hasSize(0));
        assertThat(waitingBookingDtos, hasSize(1));
    }

    @Test
    void getBookingsByBooker_whenBookerBookingsExists_thenBookingListReturned() {
        ownerDto = userService.createUser(ownerDto);
        bookerDto = userService.createUser(bookerDto);

        firstItemDto = itemService.createItem(ownerDto.getId(), firstItemDto);
        secondItemDto = itemService.createItem(bookerDto.getId(), secondItemDto);

        firstRequestBookingDto.setItemId(firstItemDto.getId());
        secondRequestBookingDto.setItemId(secondItemDto.getId());

        ResponseBookingDto firstResponseBookingDto =
            bookingService.createBooking(bookerDto.getId(), firstRequestBookingDto);
        ResponseBookingDto secondResponseBookingDto =
            bookingService.createBooking(bookerDto.getId(), secondRequestBookingDto);

        List<ResponseBookingDto> bookingDtos = List.of(firstResponseBookingDto, secondResponseBookingDto);
        List<ResponseBookingDto> actualBookingDtos =
            bookingService.getBookingsByBooker(bookerDto.getId(), BookingState.ALL);

        assertThat(actualBookingDtos, hasSize(2));

        for (ResponseBookingDto responseBookingDto : bookingDtos) {
            assertThat(actualBookingDtos, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("item", equalTo(responseBookingDto.getItem())),
                hasProperty("start", equalTo(responseBookingDto.getStart()))
            )));
        }

        List<ResponseBookingDto> pastBookingDtos =
            bookingService.getBookingsByBooker(bookerDto.getId(), BookingState.PAST);

        for (ResponseBookingDto responseBookingDto : bookingDtos) {
            assertThat(pastBookingDtos, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("item", equalTo(responseBookingDto.getItem())),
                hasProperty("start", equalTo(responseBookingDto.getStart()))
            )));
        }

        List<ResponseBookingDto> currentBookingDtos =
            bookingService.getBookingsByBooker(bookerDto.getId(), BookingState.CURRENT);
        List<ResponseBookingDto> futureBookingDtos =
            bookingService.getBookingsByBooker(bookerDto.getId(), BookingState.FUTURE);
        List<ResponseBookingDto> rejectedBookingDtos =
            bookingService.getBookingsByBooker(bookerDto.getId(), BookingState.REJECTED);
        List<ResponseBookingDto> waitingBookingDtos =
            bookingService.getBookingsByBooker(bookerDto.getId(), BookingState.WAITING);

        assertThat(currentBookingDtos, hasSize(0));
        assertThat(futureBookingDtos, hasSize(0));
        assertThat(rejectedBookingDtos, hasSize(0));
        assertThat(waitingBookingDtos, hasSize(2));
    }
}
