package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryItemRepository implements ItemRepository {
    final Map<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> findAll() {
        return items.values().stream().toList();
    }

    @Override
    public Optional<Item> findById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item save(Item item) {
        long id = nextId();
        item.setId(id);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        Item oldItem = findById(item.getId())
            .orElseThrow(() -> new NotFoundException(String.format("Item ID=%s not found", item.getId())));

        oldItem.setName(item.getName());
        oldItem.setDescription(item.getDescription());
        oldItem.setAvailable(item.isAvailable());
        return oldItem;
    }

    @Override
    public Item delete(long id) {
        return items.remove(id);
    }

    long nextId() {
        long currentMaxId = items.keySet().stream().max(Long::compareTo).orElse(0L);
        return ++currentMaxId;
    }
}
