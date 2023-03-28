package ru.practicum.shareit.server.request;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(final String message) {
        super(message);
    }
}