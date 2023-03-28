package ru.practicum.shareit.server.booking.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ShortBookingDto {
    private Long id;
    private Long bookerId;
}