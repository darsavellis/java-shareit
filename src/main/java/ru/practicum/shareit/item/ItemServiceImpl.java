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
        Item oldItem = itemRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("Item ID=%s not found", id)));

        if (oldItem.getOwner().getId() != userId) {
            throw new PermissionException("Access denied");
        }
        if (Objects.nonNull(itemDto.getName())) {
            oldItem.setName(itemDto.getName());
        }
        if (Objects.nonNull(itemDto.getDescription())) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (Objects.nonNull(itemDto.getAvailable())) {
            oldItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.mapToItemDto(itemRepository.update(oldItem));
    }

    @Override
    public ItemDto deleteItem(long id) {
        Item item = itemRepository.delete(id);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        return itemRepository.searchItems(userId, text).stream().map(ItemMapper::mapToItemDto).toList();
    }
}
