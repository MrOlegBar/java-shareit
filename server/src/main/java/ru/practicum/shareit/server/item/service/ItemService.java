package ru.practicum.shareit.server.item.service;

import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.ItemNotFoundException;
import ru.practicum.shareit.server.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    Item create(Item item);

    Item getItemByIdOrElseThrow(long itemId);

    ItemDto getItemDtoByOwnerIdAndItemId(long userId, long itemId) throws ItemNotFoundException;

    List<ItemDto> getAllItemsDtoByOwnerId(long userId, int from, int size);

    Item getItemByUserIdAndItemId(long userId, long itemId) throws ItemNotFoundException;

    Item update(Item item) throws ItemNotFoundException;

    List<Item> findItemsBySearch(String text, int from, int size);

    Comment createComment(Comment comment);
}