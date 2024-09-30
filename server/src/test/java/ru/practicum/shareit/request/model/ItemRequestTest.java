package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
class ItemRequestTest {
    @Test
    void testItemRequest() {
        ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("Description")
            .requestor(new User())
            .created(LocalDateTime.parse("2024-09-15T12:00:00"))
            .items(Collections.emptyList())
            .build();

        assertThat(itemRequest.getId(), equalTo(1L));
        assertThat(itemRequest.getDescription(), equalTo("Description"));
        assertThat(itemRequest.getRequestor(), notNullValue());
        assertThat(itemRequest.getCreated(), equalTo(LocalDateTime.parse("2024-09-15T12:00:00")));
        assertThat(itemRequest.getItems(), equalTo(Collections.emptyList()));
    }
}
