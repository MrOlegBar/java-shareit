package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql("/schema.sql")
class RequestRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private RequestRepository requestRepository;
    private final long userId = 1L;
    private final User user = new User(
            "user@user.com",
            "user");
    private final Item item = new Item(
            "Дрель",
            "Простая дрель",
            true,
            user,
            null,
            new HashSet<>());
    private final Request request = new Request(
            "Хотел бы воспользоваться щёткой для обуви",
            user,
            Set.of(item));

    @BeforeEach
    void setUp() {
        userRepository.save(user);
        itemRepository.save(item);
        requestRepository.save(request);
    }

    @Test
    public void shouldReturnRequestsByRequester_Id() {
        Collection<Request> actual = requestRepository.findAllByRequester_Id(userId);

        assertEquals(List.of(request), actual);
    }

    @Test
    public void shouldReturnRequestsByRequester_IdWithPagination() {
        int from = 0;
        int size = 10;
        List<Request> actual = requestRepository.findAllByRequester_Id(userId,
                PageRequest.of(from, size, Sort.Direction.DESC, "created"));

        assertEquals(new ArrayList<>(), actual);
    }
}
