package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
class CommentTest {
    @Test
    void testComment() {
        LocalDateTime created = LocalDateTime.parse("2024-09-15T12:00:00");
        Comment comment = new Comment(1L, "Comment", new Item(), new User(), created);
        assertThat(comment.getId(), equalTo(1L));
        assertThat(comment.getText(), equalTo("Comment"));
        assertThat(comment.getCreated(), equalTo(created));
    }
}
