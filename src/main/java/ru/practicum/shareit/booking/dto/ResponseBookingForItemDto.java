package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;

@Builder
@Data
public class ResponseBookingForItemDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private Long bookerId;
    private Item item;
}
