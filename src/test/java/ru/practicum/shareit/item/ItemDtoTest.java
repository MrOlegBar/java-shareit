package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemDtoTest {
    private final ItemDto itemDtoExpected = ItemDto.builder().build();
    private final Item item = new Item();
    private final ItemDto itemDtoOne = ItemDto.builder()
            .id(1L)
            .build();
    private final ItemDto itemDtoTwo = ItemDto.builder()
            .id(1L)
            .build();

    @Test
    void whenConvertItemEntityToItemDto_thenCorrect() {
        itemDtoExpected.setId(1L);
        itemDtoExpected.setName("Дрель");
        itemDtoExpected.setDescription("Простая дрель");
        itemDtoExpected.setAvailable(true);
        itemDtoExpected.setLastBooking(null);
        itemDtoExpected.setNextBooking(null);
        itemDtoExpected.setComments(new HashSet<>());

        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Простая дрель");
        item.setAvailable(true);
        item.setOwner(null);
        item.setRequest(null);
        item.setComments(new HashSet<>());

        ItemDto itemDtoActual = ItemMapper.toItemDto(item);

        assertEquals(itemDtoExpected.getId(), itemDtoActual.getId());
        assertEquals(itemDtoExpected.getName(), itemDtoActual.getName());
        assertEquals(itemDtoExpected.getDescription(), itemDtoActual.getDescription());
        assertEquals(itemDtoExpected.getAvailable(), itemDtoActual.getAvailable());
        assertEquals(itemDtoExpected.getComments(), itemDtoActual.getComments());
    }

    @Test
    void equalsT() {
        assertNotEquals(null, itemDtoOne);
        assertEquals(itemDtoOne, itemDtoOne);
        assertEquals(itemDtoOne, itemDtoTwo);
    }

    @Test
    void hashCodeT() {
        long expected = itemDtoOne.getId();
        long actual = itemDtoOne.hashCode();

        assertEquals(expected, actual);
    }
}
