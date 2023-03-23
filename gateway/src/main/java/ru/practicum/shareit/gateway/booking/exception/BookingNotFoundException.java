package ru.practicum.shareit.gateway.booking.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(final String message) {
        super(message);
    }
}