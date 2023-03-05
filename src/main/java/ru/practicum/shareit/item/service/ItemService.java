package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ShortItemDto;

import java.util.Collection;

public interface ItemService {
    Item create(Item item);

    Item getItemById(long itemId);

    ShortItemDto getItemDtoByItemId(long userId, long itemId) throws ItemNotFoundException;

    Collection<ShortItemDto> getAllItemsDtoByUserId(long userId);

    Item getItemByUserIdAndItemId(long userId, long itemId) throws ItemNotFoundException;

    Item update(Item item) throws ItemNotFoundException;
    Collection<Item> findItemsBySearch(String text);
}