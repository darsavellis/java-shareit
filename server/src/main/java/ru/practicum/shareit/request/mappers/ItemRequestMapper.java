package ru.practicum.shareit.request.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mappers.UserMapper;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
            .id(itemRequest.getId())
            .description(itemRequest.getDescription())
            .requestor(UserMapper.mapToUserDto(itemRequest.getRequestor()))
            .created(itemRequest.getCreated())
            .build();
    }

    public ItemRequestInfoDto mapToItemRequestInfoDto(ItemRequest itemRequest, List<ItemDto> items) {
        return ItemRequestInfoDto.builder()
            .id(itemRequest.getId())
            .description(itemRequest.getDescription())
            .requestor(UserMapper.mapToUserDto(itemRequest.getRequestor()))
            .created(itemRequest.getCreated())
            .items(items)
            .build();
    }

    public ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
            .id(itemRequestDto.getId())
            .description(itemRequestDto.getDescription())
            .requestor(UserMapper.mapToUser(itemRequestDto.getRequestor()))
            .created(itemRequestDto.getCreated())
            .build();
    }
}
