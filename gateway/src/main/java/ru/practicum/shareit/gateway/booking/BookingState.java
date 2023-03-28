package ru.practicum.shareit.gateway.booking;

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