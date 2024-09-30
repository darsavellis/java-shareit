package ru.practicum.shareit.request.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mappers.UserMapper;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
            .id(itemRequest.getId())
            .description(itemRequest.getDescription())
            .requestor(UserMapper.mapToUserDto(itemRequest.getRequestor()))
            .created(itemRequest.getCreated())
            .items(itemRequest.getItems().stream().map(ItemMapper::mapToItemDto).toList())
            .build();
    }

    public ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
            .id(itemRequestDto.getId())
            .description(itemRequestDto.getDescription())
            .requestor(UserMapper.mapToUser(itemRequestDto.getRequestor()))
            .created(itemRequestDto.getCreated())
            .items(itemRequestDto.getItems().stream().map(ItemMapper::mapToItem).toList())
            .build();
    }
}
