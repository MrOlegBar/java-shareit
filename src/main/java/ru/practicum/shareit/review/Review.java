package ru.practicum.shareit.review;

import lombok.Data;

@Data
public class Review {
    private int reviewId;
    private int userId;
    private int itemId;
    private String content;
    private Boolean isUseful;
}