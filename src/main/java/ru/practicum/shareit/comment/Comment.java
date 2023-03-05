package ru.practicum.shareit.comment;

import lombok.Data;

@Data
public class Comment {
    private int reviewId;
    private int userId;
    private int itemId;
    private String content;
    private Boolean isUseful;
}