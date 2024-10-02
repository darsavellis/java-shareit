package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.mappers.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestServiceImpl implements ItemRequestService {
    final ItemRequestRepository itemRequestRepository;
    final UserRepository userRepository;
    final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createItemRequest(long requestorId, ItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(requestorId)
            .orElseThrow(() -> new NotFoundException(String.format("Requestor with ID = %s not found", requestorId)));
        itemRequestDto.setRequestor(UserMapper.mapToUserDto(requestor));
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto);

        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestInfoDto> getItemRequestsByOwner(long requestorId) {
        List<ItemRequest> itemRequests =
            itemRequestRepository.findAllByRequestorId(requestorId);
        Map<Long, List<ItemDto>> items = itemRepository.findAll().stream()
            .map(ItemMapper::mapToItemDto)
            .filter(itemDto -> itemDto.getRequestId() != null)
            .collect(Collectors.groupingBy(ItemDto::getRequestId));
        return itemRequests.stream()
            .map(itemRequest -> {
                return ItemRequestMapper.mapToItemRequestInfoDto(itemRequest, items.get(itemRequest.getId()));
            }).toList();
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests() {
        return itemRequestRepository.findAll().stream().map(ItemRequestMapper::mapToItemRequestDto).toList();
    }

    @Override
    public ItemRequestInfoDto getItemRequestById(long id) {
        ItemRequest itemRequest = itemRequestRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("ItemRequest with ID = %s not found", id)));
        List<ItemDto> items = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
            .map(ItemMapper::mapToItemDto).toList();
        return ItemRequestMapper.mapToItemRequestInfoDto(itemRequest, items);
    }
}
