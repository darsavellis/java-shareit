package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {
    final ObjectMapper mapper;
    @MockBean
    final ItemRequestService itemRequestService;
    final MockMvc mvc;
    ItemRequestDto itemRequestDto;
    ItemRequestInfoDto itemRequestInfoDto;

    @BeforeEach
    void setUp() {
        UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        itemRequestDto = ItemRequestDto.builder()
            .description("ItemRequest description")
            .created(LocalDateTime.now())
            .requestor(userDto)
            .build();

        itemRequestInfoDto = ItemRequestInfoDto.builder()
            .description("ItemRequest description")
            .created(LocalDateTime.now())
            .requestor(userDto)
            .items(null)
            .build();
    }

    @Test
    @SneakyThrows
    void createItemRequest() {
        when(itemRequestService.createItemRequest(1L, itemRequestDto)).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                .content(mapper.writeValueAsString(itemRequestDto))
                .header("X-Sharer-User-Id", itemRequestDto.getRequestor().getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
            .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));

    }

    @Test
    @SneakyThrows
    void getAllItemRequests() {
        when(itemRequestService.getAllItemRequests()).thenReturn(Collections.emptyList());

        mvc.perform(get("/requests/all"))
            .andExpect(status().isOk())
            .andExpect(content().json(Collections.emptyList().toString()));
    }

    @Test
    @SneakyThrows
    void getItemRequestsByOwner() {
        List<ItemRequestInfoDto> itemRequestDtos = List.of(itemRequestInfoDto);
        when(itemRequestService.getItemRequestsByOwner(anyLong())).thenReturn(itemRequestDtos);

        String response = mvc.perform(get("/requests").header("X-Sharer-User-Id", 1L))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(mapper.writeValueAsString(itemRequestDtos), equalTo(response));
    }

    @Test
    @SneakyThrows
    void getItemRequestById() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        when(itemRequestService.getItemRequestById(anyLong())).thenReturn(itemRequestInfoDto);

        mvc.perform(get("/requests/" + itemRequestDto.getId()))
            .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
            .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));
    }
}
