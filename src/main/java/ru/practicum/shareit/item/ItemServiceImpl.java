package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.PermissionException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemRepository.findByUserId(userId).stream().map(ItemMapper::mapToItemDto).toList();
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        Item item = ItemMapper.mapToItem(itemDto);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User ID=%s not found", userId)));

        item.setOwner(user);
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(long id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("Item ID=%s not found", id)));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(long id, long userId, ItemDto itemDto) {
        final Item oldItem = itemRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("Item ID=%s not found", id)));
        final String name = itemDto.getName();
        final String description = itemDto.getDescription();
        final User owner = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User ID=%s not found", userId)));

        if (oldItem.getOwner().getId() != userId) {
            throw new PermissionException("Access denied");
        }
        if (name != null && !name.isBlank()) {
            oldItem.setName(itemDto.getName());
        }
        if (description != null && !description.isBlank()) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (Objects.nonNull(itemDto.getAvailable())) {
            oldItem.setAvailable(itemDto.getAvailable());
        }

        oldItem.setOwner(owner);
        return ItemMapper.mapToItemDto(oldItem);
    }

    @Override
    public ItemDto deleteItem(long id) {
        Item item = itemRepository.delete(id);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItems(userId, text).stream().map(ItemMapper::mapToItemDto).toList();
    }
}
