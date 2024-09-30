package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(long requestorId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getItemRequestsByOwner(long requestorId);

    List<ItemRequestDto> getAllItemRequests();

    ItemRequestDto getItemRequestById(long id);
}
