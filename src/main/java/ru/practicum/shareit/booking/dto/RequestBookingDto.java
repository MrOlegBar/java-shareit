package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.constraintGroup.Post;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Builder
@Getter
public class RequestBookingDto {
    private Long itemId;
    @FutureOrPresent(message = "Дата начала бронирования не должна быть в прошлом.", groups = Post.class)
    private LocalDateTime start;
    @Future(message = "Дата окончания бронирования должна быть в будущем.", groups = Post.class)
    private LocalDateTime end;
}