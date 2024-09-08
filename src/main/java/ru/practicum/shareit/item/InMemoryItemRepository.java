package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryItemRepository implements ItemRepository {
    static long currentMaxId;
    final Map<Long, Item> items = new HashMap<>();
    final Map<Long, Set<Item>> itemsByUser = new HashMap<>();

    @Override
    public Optional<Item> findById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> findByUserId(long userId) {
        return itemsByUser.getOrDefault(userId, new HashSet<>()).stream().toList();
    }

    @Override
    public List<Item> searchItems(long userId, String text) {
        return findByUserId(userId).stream()
            .filter(Item::isAvailable)
            .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())).toList();
    }

    @Override
    public Item save(Item item) {
        long id = nextId();
        item.setId(id);
        items.put(item.getId(), item);
        Set<Item> userItems = itemsByUser.computeIfAbsent(item.getOwner().getId(), userId -> new HashSet<>());
        userItems.add(item);
        return item;
    }

    @Override
    public Item update(Item item) {
        Item oldItem = items.get(item.getId());
        oldItem.setId(item.getId());
        oldItem.setName(item.getName());
        oldItem.setDescription(item.getDescription());
        oldItem.setOwner(item.getOwner());
        oldItem.setAvailable(item.isAvailable());
        return oldItem;
    }

    @Override
    public Item delete(long id) {
        Item item = items.remove(id);
        itemsByUser.get(item.getOwner().getId()).remove(item);
        return item;
    }

    long nextId() {
        return ++currentMaxId;
    }
}
