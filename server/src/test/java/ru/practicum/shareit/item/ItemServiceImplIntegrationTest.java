package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.exceptions.PermissionException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {
    final ItemService itemService;
    final UserService userService;
    final BookingService bookingService;
    final ItemRequestService itemRequestService;

    UserDto userDto;
    UserDto newUserDto;
    ItemDto itemDto;
    ItemDto newItemDto;
    RequestBookingDto requestBookingDto;
    RequestCommentDto requestCommentDto;
    ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        newUserDto = UserDto.builder()
            .name("Sergey")
            .email("sergeev@email.com")
            .build();

        itemDto = ItemDto.builder()
            .name("ItemDto name")
            .description("ItemDto description")
            .available(true)
            .requestId(null)
            .build();

        newItemDto = ItemDto.builder()
            .name("New itemDto name")
            .description("New itemDto description")
            .available(true)
            .requestId(null)
            .build();

        requestBookingDto = RequestBookingDto.builder()
            .itemId(itemDto.getId())
            .start(LocalDateTime.now().minusDays(5))
            .end(LocalDateTime.now().minusDays(3))
            .build();

        requestCommentDto = RequestCommentDto.builder()
            .text("Good review")
            .build();

        itemRequestDto = ItemRequestDto.builder()
            .requestor(userDto)
            .description("First itemDto")
            .items(new ArrayList<>())
            .build();
    }

    @Test
    void getItems() {
        userDto = userService.createUser(userDto);
        itemDto = itemService.createItem(userDto.getId(), itemDto);
        List<ItemDtoWithComments> itemDtos = itemService.getItems(userDto.getId());

        assertThat(itemDtos, hasItem(allOf(
            hasProperty("id", equalTo(itemDto.getId())),
            hasProperty("name", equalTo(itemDto.getName())),
            hasProperty("description", equalTo(itemDto.getDescription())),
            hasProperty("available", equalTo(itemDto.getAvailable())),
            hasProperty("requestId", nullValue())
        )));
    }

    @Test
    void createItem_whenUserExistAndRequestIdIsNull_thenItemDtoWithoutItemRequestReturned() {
        userDto = userService.createUser(userDto);
        ItemDto actualItemDto = itemService.createItem(userDto.getId(), itemDto);

        assertThat(actualItemDto.getId(), notNullValue());
        assertThat(actualItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(actualItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(actualItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(actualItemDto.getRequestId(), nullValue());
    }

    @Test
    void getItemById_whenItemExist_thenItemReturned() {
        userDto = userService.createUser(userDto);
        itemDto = itemService.createItem(userDto.getId(), itemDto);

        ItemDtoWithComments actualItemDto = itemService.getItemById(userDto.getId(), itemDto.getId());

        assertThat(actualItemDto.getId(), notNullValue());
        assertThat(actualItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(actualItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(actualItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(actualItemDto.getRequestId(), nullValue());
        assertThat(actualItemDto.getLastBooking(), nullValue());
        assertThat(actualItemDto.getNextBooking(), nullValue());
    }

    @Test
    void updateItem_whenItemWithoutOrWithExistsRequestId_thenItemUpdated() {
        userDto = userService.createUser(userDto);
        long userId = userDto.getId();
        itemRequestDto = itemRequestService.createItemRequest(userDto.getId(), itemRequestDto);
        long itemRequestId = itemRequestDto.getId();
        itemDto.setRequestId(itemRequestId);
        itemDto = itemService.createItem(userId, itemDto);
        newItemDto.setRequestId(itemRequestId);
        long itemId = itemDto.getId();

        ItemDto updatedItemDto = itemService.updateItem(itemId, userId, newItemDto);

        assertThat(updatedItemDto.getId(), notNullValue());
        assertThat(updatedItemDto.getName(), equalTo(newItemDto.getName()));
        assertThat(updatedItemDto.getDescription(), equalTo(newItemDto.getDescription()));
        assertThat(updatedItemDto.getAvailable(), equalTo(newItemDto.getAvailable()));
    }

    @Test
    void updateItem_whenUserHaveNoPermissions_thenThrowsException() {
        userDto = userService.createUser(userDto);
        newUserDto = userService.createUser(newUserDto);
        long userId = userDto.getId();
        itemDto = itemService.createItem(userId, itemDto);
        long itemId = itemDto.getId();
        assertThrows(PermissionException.class, () -> itemService.updateItem(itemId, newUserDto.getId(), newItemDto));
    }

    @Test
    void deleteItem_whenItemExist_thenItemDeleted() {
        userDto = userService.createUser(userDto);
        itemDto = itemService.createItem(userDto.getId(), itemDto);

        long itemId = itemDto.getId();

        ItemDto deletedItemDto = itemService.deleteItem(itemId);

        assertThat(deletedItemDto.getId(), notNullValue());
        assertThat(deletedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(deletedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(deletedItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void searchItems_whenTextIsValid_thenItemListReturnedOrEmptyList() {
        userDto = userService.createUser(userDto);
        itemDto = itemService.createItem(userDto.getId(), itemDto);
        newItemDto = itemService.createItem(userDto.getId(), newItemDto);

        List<ItemDto> resultItemDtos = itemService.searchItems("New");

        assertThat(resultItemDtos, hasSize(1));
        assertThat(resultItemDtos, hasItem(allOf(
            hasProperty("id", notNullValue()),
            hasProperty("name", equalTo(newItemDto.getName())),
            hasProperty("description", equalTo(newItemDto.getDescription())),
            hasProperty("available", equalTo(newItemDto.getAvailable()))
        )));
    }

    @Test
    void createComment_whenBookerExistAndBookedItemInPast_thenCommentReturned() {
        userDto = userService.createUser(userDto);
        long userId = userDto.getId();
        itemDto = itemService.createItem(userId, itemDto);
        long itemId = itemDto.getId();

        itemService.getItemById(userId, itemId);

        requestBookingDto.setItemId(itemId);
        bookingService.createBooking(userId, requestBookingDto);
        ResponseCommentDto responseCommentDto = itemService.createComment(userId, itemId, requestCommentDto);

        assertThat(responseCommentDto.getId(), notNullValue());
        assertThat(responseCommentDto.getText(), equalTo(requestCommentDto.getText()));
        assertThat(responseCommentDto.getAuthorName(), equalTo(userDto.getName()));
    }

    @Test
    @SneakyThrows
    void createComment_whenBookingDateNotInPast_thenThrowsValidationException() {
        userDto = userService.createUser(userDto);
        long userId = userDto.getId();
        itemDto = itemService.createItem(userId, itemDto);
        long itemId = itemDto.getId();

        itemService.getItemById(userId, itemId);
        requestBookingDto.setEnd(LocalDateTime.now().plusDays(30));
        requestBookingDto.setItemId(itemId);
        bookingService.createBooking(userId, requestBookingDto);

        assertThrows(ValidationException.class, () -> itemService.createComment(userId, itemId, requestCommentDto));
    }
}
