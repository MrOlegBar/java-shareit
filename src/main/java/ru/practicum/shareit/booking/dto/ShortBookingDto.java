package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ShortBookingDto {
    private Long id;
    private Long bookerId;

    @Override
    public String toString() {
        return "ShortBookingDto{" +
                "id=" + id +
                ", bookerId=" + bookerId +
                '}';
    }
}