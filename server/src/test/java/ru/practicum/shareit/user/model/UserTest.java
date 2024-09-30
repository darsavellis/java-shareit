package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserTest {
    @Test
    void testUser() {
        User user = User.builder()
            .id(1L)
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo("Aleksandr"));
        assertThat(user.getEmail(), equalTo("aleksandrov@email.com"));
    }
}
