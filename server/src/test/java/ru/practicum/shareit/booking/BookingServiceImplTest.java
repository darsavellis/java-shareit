package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.PermissionException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    final BookingService bookingService;
    final UserService userService;
    final ItemService itemService;

    UserDto ownerDto;
    UserDto bookerDto;
    RequestBookingDto firstRequestBookingDto;
    RequestBookingDto secondRequestBookingDto;
    ResponseBookingDto firstExpectedBookingDto;
    ResponseBookingDto secondExpectedBookingDto;
    ResponseBookingDto thirdExpectedBookingDto;

    @BeforeEach
    void setUp() {
        ownerDto = userService.getUserById(1);
        bookerDto = userService.getUserById(2);
        ItemDtoWithComments ownerItemDtoWithComments = itemService.getItemById(ownerDto.getId(), 1);
        ItemDtoWithComments bookerItemDtoWithComments = itemService.getItemById(bookerDto.getId(), 2);
        ItemDto ownerItemDto = mapToItemDto(ownerItemDtoWithComments);
        ItemDto bookerItemDto = mapToItemDto(bookerItemDtoWithComments);

        firstRequestBookingDto = RequestBookingDto.builder()
            .start(LocalDateTime.parse("2024-09-15T12:00:00"))
            .end(LocalDateTime.parse("2024-09-20T12:00:00"))
            .itemId(bookerItemDtoWithComments.getId())
            .build();

        secondRequestBookingDto = RequestBookingDto.builder()
            .start(LocalDateTime.parse("2024-12-15T12:00:00"))
            .end(LocalDateTime.parse("2024-12-20T12:00:00"))
            .itemId(ownerItemDtoWithComments.getId())
            .build();

        firstExpectedBookingDto = ResponseBookingDto.builder()
            .id(1L)
            .start(LocalDateTime.parse("2024-09-15T12:00:00"))
            .end(LocalDateTime.parse("2024-09-20T12:00:00"))
            .booker(ownerDto)
            .item(bookerItemDto)
            .status(BookingStatus.WAITING)
            .build();

        secondExpectedBookingDto = ResponseBookingDto.builder()
            .id(2L)
            .start(LocalDateTime.parse("2024-12-15T12:00:00"))
            .end(LocalDateTime.parse("2024-12-20T12:00:00"))
            .booker(bookerDto)
            .item(ownerItemDto)
            .status(BookingStatus.WAITING)
            .build();

        thirdExpectedBookingDto = ResponseBookingDto.builder()
            .id(3L)
            .start(firstRequestBookingDto.getStart())
            .end(firstRequestBookingDto.getEnd())
            .booker(ownerDto)
            .item(bookerItemDto)
            .status(BookingStatus.WAITING)
            .build();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void createBooking_whenUserAndItemExists_thenBookingSaved() {
        ResponseBookingDto responseBookingDto = bookingService.createBooking(ownerDto.getId(), firstRequestBookingDto);

        assertThat(responseBookingDto).usingRecursiveComparison().isEqualTo(thirdExpectedBookingDto);
    }

    @Test
    void createBooking_whenItemIsNotAvailable_thenThrowsNotAvailableException() {
        assertThrows(NotAvailableException.class, () -> {
            bookingService.createBooking(bookerDto.getId(), secondRequestBookingDto);
        });
    }

    @Test
    void reviewBooking_whenApprovedIsTrue_thenBookingApprovedAndReturned() {
        ResponseBookingDto responseBookingDto =
            bookingService.reviewBooking(bookerDto.getId(), firstExpectedBookingDto.getId(), true);

        assertThat(responseBookingDto.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void reviewBooking_whenApprovedIsFalse_thenBookingRejectedAndReturned() {
        ResponseBookingDto responseBookingDto =
            bookingService.reviewBooking(bookerDto.getId(), firstExpectedBookingDto.getId(), false);

        assertThat(responseBookingDto.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void reviewBooking_whenUserIsNotOwner_thenThrowsPermissionException() {
        assertThrows(PermissionException.class, () -> {
            bookingService.reviewBooking(ownerDto.getId(), firstExpectedBookingDto.getId(), true);
        });
    }

    @Test
    void getBookingById_whenUserIsOwner_thenBookingReturned() {
        long bookingId = 1L;
        ResponseBookingDto actualResponseBookingDto = bookingService.getBookingById(ownerDto.getId(), bookingId);

        assertThat(actualResponseBookingDto).usingRecursiveComparison().isEqualTo(firstExpectedBookingDto);
    }

    @Test
    void getBookingById_whenUserIsNotOwner_thenThrowsPermissionException() {
        assertThrows(PermissionException.class, () -> {
            bookingService.getBookingById(100, firstExpectedBookingDto.getId());
        });
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getBookingById_whenUserIsNotBookerAndOwner_thenThrowsPermissionException() {
        ResponseBookingDto responseBookingDto = bookingService.createBooking(bookerDto.getId(), firstRequestBookingDto);

        assertThrows(PermissionException.class, () -> bookingService.getBookingById(100L, responseBookingDto.getId()));
    }

    @Test
    void getBookingsByOwner_whenUserNotFound_thenThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingByOwner(100, BookingState.ALL));
    }

    @Test
    void getBookingsByOwner_whenBookingStateIsAll_thenOwnersBookingListWithStateAllReturned() {
        List<ResponseBookingDto> bookingDtos = List.of(secondExpectedBookingDto);
        List<ResponseBookingDto> actualBookingDtos =
            bookingService.getBookingByOwner(ownerDto.getId(), BookingState.ALL);

        assertThat(bookingDtos).usingRecursiveComparison().isEqualTo(actualBookingDtos);
    }

    @Test
    void getBookingsByOwner_whenBookingStateIsPast_thenOwnersBookingListWithStatePastReturned() {
        List<ResponseBookingDto> expectedBookingDtos = List.of(firstExpectedBookingDto);
        List<ResponseBookingDto> actualBookingDtos =
            bookingService.getBookingByOwner(bookerDto.getId(), BookingState.PAST);

        assertThat(actualBookingDtos).usingRecursiveComparison().isEqualTo(expectedBookingDtos);
    }

    @Test
    void getBookingsByOwner_whenBookingStateIsCurrent_thenOwnersBookingListWithStateCurrentReturned() {
        List<ResponseBookingDto> bookingDtos = Collections.emptyList();
        List<ResponseBookingDto> actualBookingDtos =
            bookingService.getBookingByOwner(ownerDto.getId(), BookingState.CURRENT);

        assertThat(bookingDtos).usingRecursiveComparison().isEqualTo(actualBookingDtos);
    }

    @Test
    void getBookingsByOwner_whenBookingStateIsFuture_thenOwnersBookingListWithStateCurrentFuture() {
        List<ResponseBookingDto> bookingDtos = List.of(secondExpectedBookingDto);
        List<ResponseBookingDto> actualBookingDtos =
            bookingService.getBookingByOwner(ownerDto.getId(), BookingState.FUTURE);

        assertThat(bookingDtos).usingRecursiveComparison().isEqualTo(actualBookingDtos);
    }

    @Test
    void getBookingsByOwner_whenBookingStateIsWaiting_thenOwnersBookingListWithStateWaitingReturned() {
        List<ResponseBookingDto> bookingDtos = List.of(secondExpectedBookingDto);
        List<ResponseBookingDto> actualBookingDtos =
            bookingService.getBookingByOwner(ownerDto.getId(), BookingState.WAITING);

        assertThat(bookingDtos).usingRecursiveComparison().isEqualTo(actualBookingDtos);
    }

    @Test
    void getBookingsByOwner_whenBookingStateIsRejected_thenOwnersBookingListWithStateRejectedReturned() {
        List<ResponseBookingDto> bookingDtos = Collections.emptyList();
        List<ResponseBookingDto> actualBookingDtos =
            bookingService.getBookingByOwner(ownerDto.getId(), BookingState.REJECTED);

        assertThat(bookingDtos).usingRecursiveComparison().isEqualTo(actualBookingDtos);
    }

    @Test
    void getBookingsByBooker_whenBookingStateIsAll_thenBookerBookingListWithStateAllReturned() {
        List<ResponseBookingDto> bookingDtos = List.of(secondExpectedBookingDto);
        List<ResponseBookingDto> actualBookingDtos =
            bookingService.getBookingsByBooker(bookerDto.getId(), BookingState.ALL);

        assertThat(bookingDtos).usingRecursiveComparison().isEqualTo(actualBookingDtos);
    }

    @Test
    void getBookingsByBooker_whenBookingStateIsPast_thenBookerBookingListWithStatePastReturned() {
        List<ResponseBookingDto> bookingDtos = Collections.emptyList();
        List<ResponseBookingDto> actualBookingDtos =
            bookingService.getBookingsByBooker(bookerDto.getId(), BookingState.PAST);

        assertThat(bookingDtos).usingRecursiveComparison().isEqualTo(actualBookingDtos);
    }

    @Test
    void getBookingsByBooker_whenBookingStateIsCurrent_thenBookerBookingListWithStateCurrentReturned() {
        List<ResponseBookingDto> bookingDtos = Collections.emptyList();
        List<ResponseBookingDto> actualBookingDtos =
            bookingService.getBookingsByBooker(bookerDto.getId(), BookingState.CURRENT);

        assertThat(bookingDtos).usingRecursiveComparison().isEqualTo(actualBookingDtos);
    }

    @Test
    void getBookingsByBooker_whenBookingStateIsFuture_thenBookerBookingListWithStateFutureReturned() {
        List<ResponseBookingDto> bookingDtos = List.of(secondExpectedBookingDto);
        List<ResponseBookingDto> actualBookingDtos =
            bookingService.getBookingsByBooker(bookerDto.getId(), BookingState.FUTURE);

        assertThat(bookingDtos).usingRecursiveComparison().isEqualTo(actualBookingDtos);
    }

    @Test
    void getBookingsByBooker_whenBookingStateIsWaiting_thenBookerBookingListWithStateAllReturned() {
        List<ResponseBookingDto> bookingDtos = List.of(secondExpectedBookingDto);
        List<ResponseBookingDto> actualBookingDtos =
            bookingService.getBookingsByBooker(bookerDto.getId(), BookingState.WAITING);

        assertThat(bookingDtos).usingRecursiveComparison().isEqualTo(actualBookingDtos);
    }

    @Test
    void getBookingsByBooker_whenBookingStateIsRejected_thenBookerBookingListWithStateRejectedReturned() {
        List<ResponseBookingDto> bookingDtos = Collections.emptyList();
        List<ResponseBookingDto> actualBookingDtos =
            bookingService.getBookingsByBooker(bookerDto.getId(), BookingState.REJECTED);

        assertThat(bookingDtos).usingRecursiveComparison().isEqualTo(actualBookingDtos);
    }

    ItemDto mapToItemDto(ItemDtoWithComments itemDtoWithComments) {
        return ItemDto.builder()
            .id(itemDtoWithComments.getId())
            .name(itemDtoWithComments.getName())
            .description(itemDtoWithComments.getDescription())
            .available(itemDtoWithComments.getAvailable())
            .requestId(itemDtoWithComments.getRequestId())
            .build();
    }
}
