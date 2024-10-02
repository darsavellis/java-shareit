package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.PermissionException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {
    final ItemService itemService;
    final UserService userService;

    UserDto firstUserDto;
    UserDto secondUserDto;
    ItemDto firstItemDto;
    ItemDto newItemDto;
    ItemDto secondItemDto;
    RequestCommentDto requestCommentDto;
    ItemDtoWithComments firstItemDtoWithComments;

    @BeforeEach
    void setUp() {
        firstUserDto = userService.getUserById(1L);
        secondUserDto = userService.getUserById(2L);
        firstItemDtoWithComments = itemService.getItemById(firstUserDto.getId(), 1);
        ItemDtoWithComments secondItemDtoWithComments = itemService.getItemById(secondUserDto.getId(), 2);
        firstItemDto = mapToItemDto(firstItemDtoWithComments);
        secondItemDto = mapToItemDto(secondItemDtoWithComments);

        requestCommentDto = RequestCommentDto.builder()
            .text("Good review")
            .build();

        newItemDto = ItemDto.builder()
            .id(3)
            .name("New Item")
            .description("New Item description")
            .available(true)
            .requestId(1L)
            .build();
    }

    @Test
    void getItems() {
        List<ItemDtoWithComments> itemDtos = List.of(firstItemDtoWithComments);
        List<ItemDtoWithComments> actualItemDtos = itemService.getItems(firstUserDto.getId());

        AssertionsForClassTypes.assertThat(itemDtos).usingRecursiveComparison().isEqualTo(actualItemDtos);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void createItem_whenUserExistAndRequestIdIsNull_thenItemDtoWithoutItemRequestReturned() {
        newItemDto.setRequestId(null);
        ItemDto actualItemDto = itemService.createItem(firstUserDto.getId(), newItemDto);

        AssertionsForClassTypes.assertThat(newItemDto).usingRecursiveComparison().isEqualTo(actualItemDto);

    }

    @Test
    void getItemById_whenItemExist_thenItemReturned() {
        ItemDtoWithComments actualItemDto = itemService.getItemById(firstUserDto.getId(), firstItemDto.getId());

        AssertionsForClassTypes.assertThat(firstItemDtoWithComments).usingRecursiveComparison().isEqualTo(actualItemDto);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void updateItem_whenItemWithoutOrWithExistsRequestId_thenItemUpdated() {
        firstItemDto.setName("Updated name");
        firstItemDto.setDescription("Updated description");
        ItemDto updatedItemDto = itemService.updateItem(firstItemDto.getId(), firstUserDto.getId(), firstItemDto);

        AssertionsForClassTypes.assertThat(firstItemDto).usingRecursiveComparison().isEqualTo(updatedItemDto);
    }

    @Test
    void updateItem_whenUserHaveNoPermissions_thenThrowsException() {
        secondItemDto.setName("Updated name");
        secondItemDto.setDescription("Updated description");

        assertThrows(PermissionException.class, () -> {
            itemService.updateItem(firstUserDto.getId(), secondUserDto.getId(), secondItemDto);
        });
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteItem_whenItemExist_thenItemDeleted() {
        ItemDto deletedItemDto = itemService.deleteItem(firstItemDto.getId());

        AssertionsForClassTypes.assertThat(firstItemDto).usingRecursiveComparison().isEqualTo(deletedItemDto);
    }

    @Test
    void searchItems_whenTextIsValid_thenItemListReturned() {
        List<ItemDto> expectedItemDtos = List.of(secondItemDto);

        List<ItemDto> actualItemDtos = itemService.searchItems("Not");

        AssertionsForClassTypes.assertThat(expectedItemDtos).usingRecursiveComparison().isEqualTo(actualItemDtos);
    }

    @Test
    void searchItems_whenTextIsEmpty_thenEmptyListReturned() {
        List<ItemDto> expectedItemDtos = Collections.emptyList();

        List<ItemDto> actualItemDtos = itemService.searchItems("");

        AssertionsForClassTypes.assertThat(expectedItemDtos).usingRecursiveComparison().isEqualTo(actualItemDtos);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void createComment_whenBookerExistAndBookedItemInPast_thenCommentReturned() {
        ResponseCommentDto responseCommentDto =
            itemService.createComment(firstUserDto.getId(), secondItemDto.getId(), requestCommentDto);

        AssertionsForClassTypes.assertThat(requestCommentDto).usingRecursiveComparison().isEqualTo(responseCommentDto);

    }

    @Test
    void createComment_whenBookingDateNotInPast_thenThrowsPermissionException() {
        assertThrows(PermissionException.class, () -> {
            itemService.createComment(secondUserDto.getId(), firstUserDto.getId(), requestCommentDto);
        });
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
