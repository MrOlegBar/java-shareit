package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.constraintGroup.Post;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Builder
@Getter
public class RequestBookingDto {
    private Long itemId;
    @Future(message = "Дата начала бронирования должна быть в будущем.", groups = Post.class)
    private LocalDateTime start;
    @Future(message = "Дата окончания бронирования должна быть в будущем.", groups = Post.class)
    private LocalDateTime end;
}