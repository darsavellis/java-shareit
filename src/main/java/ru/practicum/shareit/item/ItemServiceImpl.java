package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.PermissionException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final BookingRepository bookingRepository;
    final CommentRepository commentRepository;

    @Override
    public List<ItemDtoWithComments> getItems(long userId) {
        Map<Long, List<ResponseCommentDto>> commentDtoMap = commentRepository.findAll()
            .stream().map(ItemMapper::mapToCommentDto).collect(Collectors.groupingBy(ResponseCommentDto::getId));

        Map<Long, List<ResponseBookingDto>> bookingDtoMap = bookingRepository.findAllByItemOwnerId(userId)
            .stream().map(BookingMapper::mapToResponseBookingDto)
            .collect(Collectors.groupingBy(responseBookingDto -> responseBookingDto.getItem().getId()));

        Map<Long, ItemDtoWithComments> itemsByOwner = itemRepository.findByOwnerId(userId).stream()
            .map(item -> {
                ItemDtoWithComments itemDtoWithComments = ItemMapper.mapToItemDtoWithComments(item);
                itemDtoWithComments.setComments(commentDtoMap.get(item.getId()));

                LocalDateTime nextBookingDto = bookingDtoMap.getOrDefault(item.getId(), new LinkedList<>())
                    .stream().map(ResponseBookingDto::getEnd).max(LocalDateTime::compareTo).orElse(null);
                LocalDateTime lastBookingDto = bookingDtoMap.getOrDefault(item.getId(), new LinkedList<>())
                    .stream().map(ResponseBookingDto::getStart).min(LocalDateTime::compareTo).orElse(null);

                itemDtoWithComments.setNextBooking(nextBookingDto);
                itemDtoWithComments.setLastBooking(lastBookingDto);

                return itemDtoWithComments;
            }).collect(Collectors.toMap(ItemDtoWithComments::getId, Function.identity()));

        return itemsByOwner.values().stream().toList();
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        Item item = ItemMapper.mapToItem(itemDto);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User ID=%s not found", userId)));
        item.setOwner(user);
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDtoWithComments getItemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException(String.format("Item ID=%s not found", itemId)));
        List<ResponseCommentDto> commentDtos = commentRepository.findAllByItemId(itemId)
            .stream().map(ItemMapper::mapToCommentDto).toList();
        List<ResponseBookingDto> bookingDtos = bookingRepository.findAllByItemId(itemId)
            .stream().map(BookingMapper::mapToResponseBookingDto).toList();
        ItemDtoWithComments itemDtoWithComments = ItemMapper.mapToItemDtoWithComments(item);
        itemDtoWithComments.setComments(commentDtos);

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime nextBookingDto = bookingDtos
                .stream().map(ResponseBookingDto::getEnd).max(LocalDateTime::compareTo).orElse(null);
            LocalDateTime lastBookingDto = bookingDtos
                .stream().map(ResponseBookingDto::getStart).min(LocalDateTime::compareTo).orElse(null);

            itemDtoWithComments.setNextBooking(nextBookingDto);
            itemDtoWithComments.setLastBooking(lastBookingDto);
        }
        return itemDtoWithComments;
    }

    @Override
    public ItemDto updateItem(long itemId, long userId, ItemDto itemDto) {
        final Item oldItem = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException(String.format("Item ID=%s not found", itemId)));
        final String name = itemDto.getName();
        final String description = itemDto.getDescription();
        final User owner = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User ID=%s not found", userId)));

        if (oldItem.getOwner().getId() != userId) {
            throw new PermissionException("Access denied");
        }
        if (name != null && !name.isBlank()) {
            oldItem.setName(itemDto.getName());
        }
        if (description != null && !description.isBlank()) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (Objects.nonNull(itemDto.getAvailable())) {
            oldItem.setAvailable(itemDto.getAvailable());
        }

        oldItem.setOwner(owner);
        itemRepository.save(oldItem);
        return ItemMapper.mapToItemDto(oldItem);
    }

    @Override
    public ItemDto deleteItem(long itemId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException(String.format("Item ID=%s not found", itemId)));

        itemRepository.delete(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text.toUpperCase())
            .stream().map(ItemMapper::mapToItemDto).toList();
    }

    @Override
    public ResponseCommentDto createComment(long userId, long itemId, RequestCommentDto requestCommentDto) {
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())) {
            throw new PermissionException("Access denied");
        }

        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException(String.format("Item ID=%s not found", itemId)));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User ID=%s not found", userId)));
        Comment comment = ItemMapper.mapToComment(requestCommentDto, user, item);
        commentRepository.save(comment);
        return ItemMapper.mapToCommentDto(comment);
    }
}
