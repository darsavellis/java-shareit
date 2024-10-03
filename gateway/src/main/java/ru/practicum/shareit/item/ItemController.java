package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCommentDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {
    static final String USER_ID_HEADER = "X-Sharer-User-Id";
    final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemClient.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) long userId, @RequestBody ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable long itemId, @RequestHeader(USER_ID_HEADER) long userId,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable long itemId) {
        return itemClient.deleteItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        log.info("Search for items with text = {}", text);
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long itemId,
                                                @RequestBody RequestCommentDto requestCommentDto) {
        return itemClient.createComment(userId, itemId, requestCommentDto);
    }
}
