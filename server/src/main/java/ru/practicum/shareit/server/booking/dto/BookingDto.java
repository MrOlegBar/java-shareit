package ru.practicum.shareit.server.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.server.booking.model.BookingStatus;
import ru.practicum.shareit.server.item.dto.ShortItemDto;
import ru.practicum.shareit.server.user.dto.ShortUserDto;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private ShortUserDto booker;
    private ShortItemDto item;
}