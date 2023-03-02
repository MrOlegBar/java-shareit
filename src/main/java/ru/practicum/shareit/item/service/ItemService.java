package ru.practicum.shareit.item.service;

import ru.practicum.shareit.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemNotFoundException;

import java.util.Collection;

public interface ItemService {
    Item create(Item item) throws ValidationException;

    Item getItemById(long itemId);

    Collection<Item> getAllItemsByUserId(long userId);

    Item getItemByUserIdAndItemId(long userId, long itemId) throws ItemNotFoundException;

    Item update(Item item) throws ItemNotFoundException;
    Collection<Item> findItemsBySearch(String text);
}