package ru.practicum.shareit.server.item.dto.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.item.dto.ShortItemDto;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.dto.LessShortItemDto;
import ru.practicum.shareit.server.item.dto.ItemDto;

import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public static LessShortItemDto toLessShortItemDto(Item item) {
        return LessShortItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item toItem(LessShortItemDto lessShortItemDto) {
        Item item = new Item();
        item.setId(lessShortItemDto.getId());
        item.setName(lessShortItemDto.getName());
        item.setDescription(lessShortItemDto.getDescription());
        item.setAvailable(lessShortItemDto.getAvailable());
        return item;
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = ItemDto.builder().build();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setComments(item.getComments()
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toSet()));
        return itemDto;
    }

    public static ShortItemDto toShortItemDto(Item item) {
        return ShortItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }
}