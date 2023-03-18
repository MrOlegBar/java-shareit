package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class ItemTest {
    private final Item itemOne = new Item(
            "Дрель",
            "Простая дрель",
            true,
            null,
            null,
            new HashSet<>());
    private final Item itemTwo = new Item(
            1L,
            "Дрель",
            "Простая дрель",
            true,
            null,
            null,
            new HashSet<>());

    @BeforeEach
    void setUp() {
        itemOne.setId(1L);
    }

    @Test
    void equalsT() {
        assertNotEquals(null, itemOne);
        assertEquals(itemOne, itemOne);
        assertEquals(itemOne, itemTwo);
    }

    @Test
    void hashCodeT() {
        long expected = itemOne.getId();
        long actual = itemOne.hashCode();

        assertEquals(expected, actual);
    }

    @Test
    void toStringT() {
        String expected = itemOne.toString();
        String actual = "Item(id=1, name=Дрель, description=Простая дрель, available=true, owner=null, request=null, comments=[])";

        assertEquals(expected, actual);
    }
}
