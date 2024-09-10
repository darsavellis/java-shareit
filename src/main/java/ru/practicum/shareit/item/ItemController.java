package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable long id) {
        return itemService.getItemById(id);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) long userId, @Validated @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable long id, @RequestHeader(USER_ID_HEADER) long userId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(id, userId, itemDto);
    }

    @DeleteMapping("/{id}")
    public ItemDto deleteItem(@PathVariable long id) {
        return itemService.deleteItem(id);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(USER_ID_HEADER) long userId, @RequestParam String text) {
        return itemService.searchItems(userId, text);
    }
}
