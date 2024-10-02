package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    final ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    final MockMvc mvc;

    ItemDto itemDto;
    ItemDto newItemDto;
    ItemDtoWithComments itemDtoWithComments;
    final long userId = 1L;
    final long itemId = 1L;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
            .id(itemId)
            .name("ItemDto name")
            .description("ItemDto description")
            .available(true)
            .requestId(userId)
            .build();

        newItemDto = ItemDto.builder()
            .id(itemId)
            .name("New itemDto")
            .description("New itemDto description")
            .available(true)
            .requestId(userId)
            .build();

        itemDtoWithComments = ItemDtoWithComments.builder()
            .id(itemId)
            .name(itemDto.getName())
            .description(itemDto.getDescription())
            .available(itemDto.getAvailable())
            .requestId(itemDto.getRequestId())
            .build();
    }

    @Test
    @SneakyThrows
    void getItems() {
        when(itemService.getItems(anyLong())).thenReturn(List.of(itemDtoWithComments));

        String response = mvc.perform(get("/items")
                .header("X-Sharer-User-Id", userId))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(mapper.writeValueAsString(List.of(itemDtoWithComments)), equalTo(response));
    }

    @Test
    @SneakyThrows
    void getItemById() {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDtoWithComments);

        mvc.perform(get("/items/" + itemDto.getId()).header("X-Sharer-User-Id", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue(), Long.class))
            .andExpect(jsonPath("$.name", equalTo(itemDtoWithComments.getName()), String.class))
            .andExpect(jsonPath("$.description", equalTo(itemDtoWithComments.getDescription()), String.class));
    }

    @Test
    @SneakyThrows
    void createItem() {
        when(itemService.createItem(userId, itemDto)).thenReturn(itemDto);

        mvc.perform(post("/items")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(itemDto))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue(), Long.class))
            .andExpect(jsonPath("$.name", equalTo(itemDto.getName()), String.class))
            .andExpect(jsonPath("$.description", equalTo(itemDto.getDescription()), String.class));
    }

    @Test
    @SneakyThrows
    void updateItem() {
        when(itemService.updateItem(itemId, userId, itemDto)).thenReturn(newItemDto);

        mvc.perform(patch("/items/" + itemId)
                .header("X-Sharer-User-Id", userId)
                .content(mapper.writeValueAsString(itemDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue(), Long.class))
            .andExpect(jsonPath("$.name", equalTo(newItemDto.getName()), String.class))
            .andExpect(jsonPath("$.description", equalTo(newItemDto.getDescription()), String.class));
    }

    @Test
    @SneakyThrows
    void deleteItem() {
        when(itemService.deleteItem(itemId)).thenReturn(itemDto);

        mvc.perform(delete("/items/" + itemId))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void search() {
        when(itemService.searchItems(anyString())).thenReturn(List.of(itemDto));

        String response = mvc.perform(get("/items/search").param("text", "criteria"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(mapper.writeValueAsString(List.of(itemDto)), equalTo(response));
    }

    @Test
    @SneakyThrows
    void createComment() {
        RequestCommentDto requestCommentDto = RequestCommentDto.builder()
            .text("Good thing")
            .build();
        ResponseCommentDto responseCommentDto = ResponseCommentDto.builder()
            .id(1L)
            .text(requestCommentDto.getText())
            .authorName("Aleksandr")
            .created(LocalDateTime.now())
            .build();

        when(itemService.createComment(userId, itemId, requestCommentDto)).thenReturn(responseCommentDto);

        mvc.perform(post("/items/" + itemId + "/comment")
                .content(mapper.writeValueAsString(requestCommentDto))
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue(), Long.class))
            .andExpect(jsonPath("$.text", equalTo(responseCommentDto.getText()), String.class))
            .andExpect(jsonPath("$.authorName", equalTo(responseCommentDto.getAuthorName()), String.class));
    }
}
