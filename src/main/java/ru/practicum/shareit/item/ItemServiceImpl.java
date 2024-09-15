package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.PermissionException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
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
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final BookingRepository bookingRepository;
    final CommentRepository commentRepository;

    @Override
    public List<CommentItemDto> getItems(long userId) {
        Map<Long, CommentItemDto> itemsByOwner = itemRepository.findByOwnerId(userId).stream()
            .map(ItemMapper::mapToCommentItemDto)
            .collect(Collectors.toMap(CommentItemDto::getId, Function.identity()));

        commentRepository.findAllByItemIdIn(itemsByOwner.keySet().stream().toList()).forEach(comment -> {
            itemsByOwner.get(comment.getItem().getId()).getComments().add(ItemMapper.mapToCommentDto(comment));
        });

        bookingRepository.findAllByItemOwnerId(userId).forEach(booking -> {
            addLastAndNextBookings(booking, itemsByOwner);
        });

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
    public CommentItemDto getItemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException(String.format("Item ID=%s not found", itemId)));
        CommentItemDto commentItemDto = ItemMapper.mapToCommentItemDto(item);
        commentItemDto.getComments()
            .addAll(commentRepository.findAllByItemId(itemId).stream().map(ItemMapper::mapToCommentDto).toList());

        if (item.getOwner().getId().equals(userId)) {
            bookingRepository.findAllByItemId(userId).forEach(booking -> {
                addLastAndNextBookings(booking, new HashMap<>(Map.of(itemId, commentItemDto)));
            });
        }
        return commentItemDto;
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
            throw new ValidationException("");
        }
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException(String.format("Item ID=%s not found", itemId)));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User ID=%s not found", userId)));
        Comment comment = ItemMapper.mapToComment(requestCommentDto, user, item);
        commentRepository.save(comment);
        return ItemMapper.mapToCommentDto(comment);
    }

    void addLastAndNextBookings(Booking booking, Map<Long, CommentItemDto> itemDtoMap) {
        long itemId = booking.getItem().getId();
        itemDtoMap.get(itemId).setLastBooking(getLastBooking(booking, itemDtoMap.get(itemId)));
        itemDtoMap.get(itemId).setNextBooking(getNextBooking(booking, itemDtoMap.get(itemId)));
    }

    LocalDateTime getNextBooking(Booking booking, CommentItemDto ownerCommentItemDto) {
        return Stream.of(ownerCommentItemDto.getNextBooking(), booking.getStart())
            .filter(Objects::nonNull).filter(localDateTime -> localDateTime.isAfter(LocalDateTime.now()))
            .min(LocalDateTime::compareTo).orElse(null);
    }

    LocalDateTime getLastBooking(Booking booking, CommentItemDto ownerCommentItemDto) {
        return Stream.of(ownerCommentItemDto.getLastBooking(), booking.getEnd())
            .filter(Objects::nonNull).filter(localDateTime -> localDateTime.isBefore(LocalDateTime.now()))
            .max(LocalDateTime::compareTo).orElse(null);
    }
}
