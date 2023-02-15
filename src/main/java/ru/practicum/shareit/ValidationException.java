package ru.practicum.shareit;

public class ValidationException extends RuntimeException {
    public ValidationException(final String message) {
        super(message);
    }
}