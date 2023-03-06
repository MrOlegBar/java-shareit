package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Item create(Item item);

    Item getItemById(long itemId);

    ItemDto getItemDtoByItemId(long userId, long itemId) throws ItemNotFoundException;

    Collection<ItemDto> getAllItemsDtoByUserId(long userId);

    Item getItemByUserIdAndItemId(long userId, long itemId) throws ItemNotFoundException;

    Item update(Item item) throws ItemNotFoundException;

    Collection<Item> findItemsBySearch(String text);

    Comment createComment(Comment comment);
}