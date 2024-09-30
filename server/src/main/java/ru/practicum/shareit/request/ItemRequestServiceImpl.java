package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mappers.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

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
    public List<ItemRequestDto> getItemRequestsByOwner(long requestorId) {
        List<ItemRequest> itemRequests =
            itemRequestRepository.findAllByRequestorId(requestorId);
        return itemRequests.stream().map(ItemRequestMapper::mapToItemRequestDto).toList();
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests() {
        return itemRequestRepository.findAll().stream().map(ItemRequestMapper::mapToItemRequestDto).toList();
    }

    @Override
    public ItemRequestDto getItemRequestById(long id) {
        ItemRequest itemRequest = itemRequestRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("ItemRequest with ID = %s not found", id)));
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }
}
