package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(long requestorId, ItemRequestDto itemRequestDto);

    List<ItemRequestInfoDto> getItemRequestsByOwner(long requestorId);

    List<ItemRequestInfoDto> getAllItemRequests(long userId);

    ItemRequestInfoDto getItemRequestById(long id);
}
