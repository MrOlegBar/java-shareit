package ru.practicum.shareit.server.error;

public class MethodParametersException extends RuntimeException {
    public MethodParametersException(final String message) {
        super(message);
    }
}