package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.LessShortItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LessShortItemDtoTest {
    private final LessShortItemDto lessShortItemDto = LessShortItemDto.builder().build();
    private final LessShortItemDto lessShortItemDtoExpected = LessShortItemDto.builder()
            .id(1L)
            .name("Дрель")
            .description("Простая дрель")
            .available(true)
            .requestId(null)
            .build();
    private final Item item = new Item();
    private final Item itemExpected = new Item(
            1L,
            "Дрель",
            "Простая дрель",
            true,
            null,
            null,
            new HashSet<>());

    @Test
    void whenConvertItemEntityToLessShortItemDto_thenCorrect() {
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Простая дрель");
        item.setAvailable(true);
        item.setOwner(null);
        item.setRequest(null);
        item.setComments(new HashSet<>());

        LessShortItemDto lessShortItemDtoActual = ItemMapper.toLessShortItemDto(item);

        assertEquals(lessShortItemDtoExpected.getId(), lessShortItemDtoActual.getId());
        assertEquals(lessShortItemDtoExpected.getName(), lessShortItemDtoActual.getName());
        assertEquals(lessShortItemDtoExpected.getDescription(), lessShortItemDtoActual.getDescription());
        assertEquals(lessShortItemDtoExpected.getAvailable(), lessShortItemDtoActual.getAvailable());
        assertEquals(lessShortItemDtoExpected.getRequestId(), lessShortItemDtoActual.getRequestId());
    }

    @Test
    void whenConvertLessShortItemDtoItemEntity_thenCorrect() {
        lessShortItemDto.setId(1L);
        lessShortItemDto.setName("Дрель");
        lessShortItemDto.setDescription("Простая дрель");
        lessShortItemDto.setAvailable(true);
        lessShortItemDto.setRequestId(null);

        Item itemActual = ItemMapper.toItem(lessShortItemDto);

        assertEquals(itemExpected.getId(), itemActual.getId());
        assertEquals(itemExpected.getName(), itemActual.getName());
        assertEquals(itemExpected.getDescription(), itemActual.getDescription());
        assertEquals(itemExpected.getAvailable(), itemActual.getAvailable());
        assertEquals(itemExpected.getOwner(), itemActual.getOwner());
        assertEquals(itemExpected.getRequest(), itemActual.getRequest());
        assertEquals(itemExpected.getComments(), itemActual.getComments());
    }
}
