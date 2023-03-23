package ru.practicum.shareit.server.booking.exception;

public class BookingBadRequestException extends RuntimeException {
    public BookingBadRequestException(final String message) {
        super(message);
    }
}