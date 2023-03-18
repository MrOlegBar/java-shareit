package ru.practicum.shareit.item;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql("/schema.sql")
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private final long userId = 1L;
    private final int from = 0;
    private final int size = 10;
    private final User user = new User(
            "user@user.com",
            "user");
    private final Item item = new Item();

    @BeforeEach
    void setUp() {
        User savedUser = userRepository.save(user);

        item.setName("Дрель");
        item.setDescription("Простая дрель");
        item.setAvailable(true);
        item.setOwner(savedUser);

        itemRepository.save(item);
    }

    @Test
    @DirtiesContext
    public void shouldReturnItemByOwner_IdAndId() {
        long itemId = 1L;

        Optional<Item> actual = itemRepository.findItemByOwner_IdAndId(userId, itemId);

        assertEquals(Optional.of(item), actual);
    }

    @Test
    @DirtiesContext
    public void shouldReturnAllItemByOwner_Id() {
        List<Item> actual = itemRepository.findAllByOwner_Id(userId, PageRequest.of(from, size));

        assertEquals(List.of(item), actual);
    }

    @Test
    @DirtiesContext
    public void shouldReturnAllItemBySearch() {
        String text = "дрель";

        List<Item> actual = itemRepository.findAllBySearch(text, PageRequest.of(from, size));

        assertEquals(List.of(item), actual);
    }
}