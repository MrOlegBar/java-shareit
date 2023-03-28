package ru.practicum.shareit.gateway.request;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(final String message) {
        super(message);
    }
}