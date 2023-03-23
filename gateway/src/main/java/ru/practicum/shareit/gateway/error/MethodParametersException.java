package ru.practicum.shareit.gateway.error;

public class MethodParametersException extends RuntimeException {
    public MethodParametersException(final String message) {
        super(message);
    }
}