package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> findById(long id);

    Item save(Item item);

    Item delete(long id);

    List<Item> findByUserId(long userId);

    List<Item> searchItems(long userId, String text);
}
