package ru.practicum.shareit.server.booking.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}