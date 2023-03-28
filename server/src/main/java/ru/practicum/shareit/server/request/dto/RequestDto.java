package ru.practicum.shareit.server.request.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.server.item.dto.LessShortItemDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Builder
@Getter
public class RequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Collection<LessShortItemDto> items;
}