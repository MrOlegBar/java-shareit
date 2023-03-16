package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.LessShortItemDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Builder
@Getter
@Setter
public class RequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Collection<LessShortItemDto> items;
}