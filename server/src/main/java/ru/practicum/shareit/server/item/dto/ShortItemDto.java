package ru.practicum.shareit.server.item.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ShortItemDto {
    private Long id;
    private String name;
}