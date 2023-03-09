package ru.practicum.shareit.error;

public class MethodParametersException extends RuntimeException {
    public MethodParametersException(final String message) {
        super(message);
    }
}