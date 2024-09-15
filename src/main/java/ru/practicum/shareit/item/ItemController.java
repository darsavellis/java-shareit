package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {
    static final String userIdHeader = "X-Sharer-User-Id";
    final ItemService itemService;

    @GetMapping
    public List<CommentItemDto> getItems(@RequestHeader(userIdHeader) long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public CommentItemDto getItemById(@RequestHeader(userIdHeader) long userId, @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(userIdHeader) long userId, @Validated @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestHeader(userIdHeader) long userId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ItemDto deleteItem(@PathVariable long itemId) {
        return itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseCommentDto createComment(@RequestHeader(userIdHeader) long userId, @PathVariable long itemId,
                                            @Validated @RequestBody RequestCommentDto requestCommentDto) {
        return itemService.createComment(userId, itemId, requestCommentDto);
    }
}
