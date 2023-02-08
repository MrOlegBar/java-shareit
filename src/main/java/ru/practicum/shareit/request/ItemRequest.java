package ru.practicum.shareit.request;

import lombok.Data;

@Data
public class ItemRequest {
    private int requestId;
    private String itemName;
    private String itemDescription;
    private int userId;
}