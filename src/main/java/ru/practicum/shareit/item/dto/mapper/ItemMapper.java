package ru.practicum.shareit.item.dto.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.LessShortItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

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

    public static ShortItemDto toShortItemDto(Item item) {
        return ShortItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(item.getComments()
                        .stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toSet()))
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
}