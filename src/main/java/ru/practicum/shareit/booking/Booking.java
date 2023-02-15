package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Booking {
    int id;
    int item;
    LocalDateTime start;
    LocalDateTime end;
    Boolean isApproved;
    int user;
}
