package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {
    final ItemService itemService;
    final UserService userService;
    final ItemRequestService itemRequestService;

    UserDto ownerDto;
    UserDto userDto;
    ItemRequestDto userItemRequestDto;
    ItemRequestDto ownerItemRequestDto;
    ItemRequestDto newUserItemRequestDto;
    ItemRequestInfoDto userItemRequestInfoDto;
    ItemRequestInfoDto ownerItemRequestInfoDto;

    @BeforeEach
    void setUp() {
        ownerDto = userService.getUserById(1L);
        userDto = userService.getUserById(2L);
        ItemDtoWithComments ownerItemDtoWithComments = itemService.getItemById(ownerDto.getId(), 1);
        ItemDtoWithComments userItemDtoWithComments = itemService.getItemById(userDto.getId(), 2);
        ItemDto ownerItemDto = mapToItemDto(ownerItemDtoWithComments);
        ItemDto userItemDto = mapToItemDto(userItemDtoWithComments);

        ownerItemRequestDto = ItemRequestDto.builder()
            .id(1)
            .description("Owner ItemRequest")
            .created(LocalDateTime.parse("2024-09-15T12:00:00"))
            .requestor(ownerDto)
            .build();

        userItemRequestDto = ItemRequestDto.builder()
            .id(2)
            .description("User ItemRequest")
            .created(LocalDateTime.parse("2024-09-15T12:00:00"))
            .requestor(userDto)
            .build();

        ownerItemRequestInfoDto = ItemRequestInfoDto.builder()
            .id(1)
            .description("Owner ItemRequest")
            .created(LocalDateTime.parse("2024-09-15T12:00:00"))
            .requestor(ownerDto)
            .items(List.of(userItemDto))
            .build();

        userItemRequestInfoDto = ItemRequestInfoDto.builder()
            .id(2)
            .description("User ItemRequest")
            .created(LocalDateTime.parse("2024-09-15T12:00:00"))
            .requestor(userDto)
            .items(List.of(ownerItemDto))
            .build();

        newUserItemRequestDto = ItemRequestDto.builder()
            .id(3)
            .description("New User ItemRequest")
            .created(LocalDateTime.parse("2024-09-15T12:00:00"))
            .requestor(userDto)
            .build();
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void createItemRequest_whenUserExist_thenItemRequestSaved() {
        ItemRequestDto actualUserItemRequestDto =
            itemRequestService.createItemRequest(userDto.getId(), newUserItemRequestDto);

        assertThat(newUserItemRequestDto).usingRecursiveComparison().isEqualTo(actualUserItemRequestDto);
    }

    @Test
    void getItemRequestsByOwner_whenOwnerHasItemRequests_thenOwnerItemRequestListReturned() {
        List<ItemRequestInfoDto> actualItemRequestDtos = itemRequestService.getItemRequestsByOwner(ownerDto.getId());
        List<ItemRequestInfoDto> expectedItemRequestDtos = List.of(ownerItemRequestInfoDto);

        assertThat(actualItemRequestDtos).usingRecursiveComparison().isEqualTo(expectedItemRequestDtos);
    }

    @Test
    void getAllItemRequests_whenItemRequestExists_thenItemRequestListReturned() {
        List<ItemRequestInfoDto> expectedItemRequestDtos = List.of(ownerItemRequestInfoDto);

        List<ItemRequestInfoDto> actualItemRequestDtos = itemRequestService.getAllItemRequests(2L);

        assertThat(actualItemRequestDtos).usingRecursiveComparison().isEqualTo(expectedItemRequestDtos);

    }

    @Test
    void getItemRequestById_whenItemRequestExist_thenItemRequestReturned() {
        ItemRequestInfoDto actualItemRequestDto = itemRequestService.getItemRequestById(userItemRequestDto.getId());

        assertThat(actualItemRequestDto).usingRecursiveComparison().isEqualTo(userItemRequestInfoDto);

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
