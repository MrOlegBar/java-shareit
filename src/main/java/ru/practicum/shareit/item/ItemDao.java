package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemDao {
    Item save(Item item);

    Collection<Item> getAllItemsByUserId(long userId);

    Item findByItemId(long itemId) throws ItemNotFoundException;

    Item update(Long userId, Item item) throws ItemNotFoundException;

    Collection<Item> findItemsBySearch(String text);
}