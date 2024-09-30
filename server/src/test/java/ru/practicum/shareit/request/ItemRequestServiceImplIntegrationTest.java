package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mappers.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {
    final ItemRequestRepository itemRequestRepository;
    final UserRepository userRepository;
    final ItemRequestService itemRequestService;

    UserDto userDto;
    UserDto ownerDto;
    ItemRequestDto userItemRequestDto;
    ItemRequestDto ownerItemRequestDto;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();

        userDto = UserDto.builder()
            .name("Aleksandr")
            .email("aleksandrov@email.com")
            .build();

        ownerDto = UserDto.builder()
            .name("Sergey")
            .email("sergeev@email.com")
            .build();

        userItemRequestDto = ItemRequestDto.builder()
            .description("User itemRequest")
            .created(LocalDateTime.parse("2024-08-15T12:00:00"))
            .requestor(userDto)
            .items(Collections.emptyList())
            .build();

        ownerItemRequestDto = ItemRequestDto.builder()
            .description("Owner itemRequest")
            .created(LocalDateTime.parse("2024-08-15T12:00:00"))
            .requestor(ownerDto)
            .items(Collections.emptyList())
            .build();

        userDto = UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUser(userDto)));
        ownerDto = UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUser(ownerDto)));
        userItemRequestDto.setRequestor(userDto);
        ownerItemRequestDto.setRequestor(ownerDto);
        userItemRequestDto = ItemRequestMapper
            .mapToItemRequestDto(itemRequestRepository.save(ItemRequestMapper.mapToItemRequest(userItemRequestDto)));
        ownerItemRequestDto = ItemRequestMapper
            .mapToItemRequestDto(itemRequestRepository.save(ItemRequestMapper.mapToItemRequest(ownerItemRequestDto)));
    }


    @Test
    void createItemRequest_whenUserExist_thenItemRequestSaved() {
        ItemRequestDto actualItemRequestDto = itemRequestService
            .createItemRequest(userItemRequestDto.getRequestor().getId(), userItemRequestDto);

        assertThat(actualItemRequestDto.getId(), notNullValue());
        assertThat(actualItemRequestDto.getDescription(), equalTo(userItemRequestDto.getDescription()));
        assertThat(actualItemRequestDto.getCreated(), equalTo(userItemRequestDto.getCreated()));
        assertThat(actualItemRequestDto.getRequestor(), allOf(
            hasProperty("name", equalTo(userDto.getName())),
            hasProperty("email", equalTo(userDto.getEmail()))
        ));
    }

    @Test
    void getItemRequestsByOwner_whenOwnerHasItemRequests_thenOwnerItemRequestListReturned() {
        List<ItemRequestDto> actualItemRequestDtos = itemRequestService.getItemRequestsByOwner(ownerDto.getId());

        assertThat(actualItemRequestDtos, hasSize(1));
        assertThat(actualItemRequestDtos, hasItem(allOf(
            hasProperty("description", equalTo(ownerItemRequestDto.getDescription())),
            hasProperty("created", equalTo(ownerItemRequestDto.getCreated())),
            hasProperty("requestor", allOf(
                hasProperty("name", equalTo(ownerDto.getName())),
                hasProperty("email", equalTo(ownerDto.getEmail()))
            )),
            hasProperty("items", hasSize(0))
        )));
    }

    @Test
    void getAllItemRequests_whenItemRequestExists_thenItemRequestListReturned() {
        List<ItemRequestDto> itemRequestDtos = List.of(userItemRequestDto, ownerItemRequestDto);
        List<ItemRequestDto> actualItemRequestDtos = itemRequestService.getAllItemRequests();

        assertThat(actualItemRequestDtos, hasSize(2));
        for (ItemRequestDto itemRequestDto : itemRequestDtos) {
            assertThat(actualItemRequestDtos, hasItems(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(itemRequestDto.getDescription())),
                hasProperty("created", equalTo(itemRequestDto.getCreated())),
                hasProperty("requestor", allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(itemRequestDto.getRequestor().getName())),
                    hasProperty("email", equalTo(itemRequestDto.getRequestor().getEmail()))
                )),
                hasProperty("items", hasSize(0))
            )));
        }
    }

    @Test
    void getItemRequestById_whenItemRequestExist_thenItemRequestReturned() {
        ItemRequestDto actualItemRequestDto = itemRequestService.getItemRequestById(userItemRequestDto.getId());

        assertThat(actualItemRequestDto, notNullValue());
        assertThat(actualItemRequestDto.getId(), notNullValue());
        assertThat(actualItemRequestDto.getDescription(), equalTo(userItemRequestDto.getDescription()));
        assertThat(actualItemRequestDto.getCreated(), equalTo(userItemRequestDto.getCreated()));
        assertThat(actualItemRequestDto.getRequestor(), allOf(
            hasProperty("name", equalTo(userDto.getName())),
            hasProperty("email", equalTo(userDto.getEmail()))
        ));
        assertThat(actualItemRequestDto.getItems(), hasSize(0));
    }

    @AfterEach
    void postSetUp() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}
