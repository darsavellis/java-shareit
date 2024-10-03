package ru.practicum.shareit.item.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
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
        ItemDto itemDto = ItemDto.builder()
            .id(item.getId())
            .name(item.getName())
            .description(item.getDescription())
            .available(item.isAvailable())
            .build();

        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }

        return itemDto;
    }


    public ItemDtoWithComments mapToItemDtoWithComments(Item item) {
        ItemDtoWithComments itemDtoWithComments = new ItemDtoWithComments();
        itemDtoWithComments.setId(item.getId());
        itemDtoWithComments.setName(item.getName());
        itemDtoWithComments.setDescription(item.getDescription());
        itemDtoWithComments.setAvailable(item.isAvailable());

        if (item.getRequest() != null) {
            itemDtoWithComments.setRequestId(item.getRequest().getId());
        }

        return itemDtoWithComments;
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
