package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.constraintGroup.Post;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class BookingDtoForRequest {
    private Long itemId;
    @FutureOrPresent(message = "Дата начала бронирования не должна быть в прошлом.", groups = Post.class)
    @NotNull(message = "Дата начала бронирования отсутствует.", groups = Post.class)
    private LocalDateTime start;
    @Future(message = "Дата окончания бронирования должна быть в будущем.", groups = Post.class)
    @NotNull(message = "Дата окончания бронирования отсутствует.", groups = Post.class)
    private LocalDateTime end;
}