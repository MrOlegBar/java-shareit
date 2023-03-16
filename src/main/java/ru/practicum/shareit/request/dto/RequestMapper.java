package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.request.Request;

import java.util.stream.Collectors;

@Component
public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(request.getItems()
                        .stream()
                        .map(ItemMapper::toLessShortItemDto)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static Request toRequest(ShortRequestDto shortRequestDto) {
        Request request = new Request();
        request.setDescription(shortRequestDto.getDescription());
        return request;
    }
}