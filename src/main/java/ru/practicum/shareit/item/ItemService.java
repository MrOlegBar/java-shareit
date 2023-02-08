package ru.practicum.shareit.item;

import ru.practicum.shareit.ValidationException;

import java.util.Collection;

public interface ItemService {
    Item create(Item item) throws ValidationException;

    Collection<Item> getAllItemsByUserId(long userId);

    Item getItemById(long itemId) throws ItemNotFoundException;

    Item update(Long userId, Item item) throws ItemNotFoundException;

    Collection<Item> findItemsBySearch(String text);
}