package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestController {
    final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Create ItemRequest {}, userId = {}", itemRequestDto, userId);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests() {
        log.info("Get all ItemRequests");
        return itemRequestClient.getAllItemRequests();
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get ItemRequests by owner, userId = {}", userId);
        return itemRequestClient.getItemRequestsByOwner(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable long requestId) {
        log.info("Get ItemRequest with ID = {}", requestId);
        return itemRequestClient.getItemRequestById(requestId);
    }
}
