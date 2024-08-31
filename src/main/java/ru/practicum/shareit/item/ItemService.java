package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(long userId);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto getItemById(long id);

    ItemDto updateItem(long id, long userId, ItemDto itemDto);

    ItemDto deleteItem(long id);

    List<ItemDto> searchItems(long userId, String text);
}
