package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;

import java.util.List;

public interface ItemService {
    List<CommentItemDto> getItems(long userId);

    ItemDto createItem(long userId, ItemDto itemDto);

    CommentItemDto getItemById(long userId, long itemId);

    ItemDto updateItem(long itemId, long userId, ItemDto itemDto);

    ItemDto deleteItem(long itemId);

    List<ItemDto> searchItems(String text);

    ResponseCommentDto createComment(long userId, long itemId, RequestCommentDto requestCommentDto);
}
