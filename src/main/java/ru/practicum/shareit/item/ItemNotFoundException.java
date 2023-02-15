package ru.practicum.shareit.item;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(final String message) {
        super(message);
    }
}