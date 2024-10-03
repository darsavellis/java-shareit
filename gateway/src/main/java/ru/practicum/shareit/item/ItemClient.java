package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCommentDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
            .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
            .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
            .build());
    }

    public ResponseEntity<Object> getItems(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemById(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> createItem(long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(long userId, ItemDto itemDto) {
        return patch("/" + itemDto.getId(), userId, itemDto);
    }

    public ResponseEntity<Object> deleteItem(long itemId) {
        return delete("/" + itemId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        Map<String, Object> params = Map.of("text", text);
        return get("/search?text={text}", null, params);
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, RequestCommentDto requestCommentDto) {
        return post("/" + itemId + "/comment", userId, requestCommentDto);
    }
}
