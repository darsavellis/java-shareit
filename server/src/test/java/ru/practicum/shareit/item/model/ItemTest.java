package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
class ItemTest {
    @Test
    void testItem() {
        Item item = new Item(1L, "Item name", "Item description", true, new User(), new ItemRequest());
        assertThat(item.getId(), equalTo(1L));
        assertThat(item.getName(), equalTo("Item name"));
        assertThat(item.getDescription(), equalTo("Item description"));
        assertThat(item.isAvailable(), equalTo(true));
    }
}
