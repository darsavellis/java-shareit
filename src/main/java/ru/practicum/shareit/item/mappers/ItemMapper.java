package ru.practicum.shareit.item.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class ItemMapper {
    public Item mapToItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        return itemDto;
    }


    public CommentItemDto mapToCommentItemDto(Item item) {
        CommentItemDto commentItemDto = new CommentItemDto();
        commentItemDto.setId(item.getId());
        commentItemDto.setName(item.getName());
        commentItemDto.setDescription(item.getDescription());
        commentItemDto.setAvailable(item.isAvailable());
        return commentItemDto;
    }

    public Comment mapToComment(RequestCommentDto requestCommentDto, User user, Item item) {
        Comment comment = new Comment();
        comment.setText(requestCommentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        return comment;
    }

    public ResponseCommentDto mapToCommentDto(Comment comment) {
        ResponseCommentDto responseCommentDto = new ResponseCommentDto();
        responseCommentDto.setId(comment.getId());
        responseCommentDto.setText(comment.getText());
        responseCommentDto.setAuthorName(comment.getAuthor().getName());
        responseCommentDto.setCreated(comment.getCreated());
        return responseCommentDto;
    }
}
