package ru.practicum.shareit.server.booking.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(final String message) {
        super(message);
    }
}