package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.exception.BookingBadRequestException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constraintGroup.Post;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.net.http.HttpHeaders;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class BookingController {
    private final UserService userService;
    private final BookingService bookingService;

    @Autowired
    public BookingController(@Qualifier("UserServiceImpl") UserService userService,
                             @Qualifier("BookingServiceImpl") BookingService bookingService) {
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    public ResponseBookingDto postBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Validated(Post.class) @RequestBody RequestBookingDto requestBookingDto)
            throws UserNotFoundException, BookingBadRequestException {
        User booker = userService.getUserById(userId);
        Booking bookingFromDto = BookingMapper.toBooking(requestBookingDto);

        if (!bookingFromDto.getItem().getAvailable()) {
            log.debug("Вещь с itemId  = {} не доступна для бронирования.", bookingFromDto.getItem().getId());
            throw new BookingBadRequestException(String.format("Вещь с itemId = %s не доступна для бронирования.",
                    bookingFromDto.getItem().getId()));
        }

        if (bookingFromDto.getStartDate().isAfter(bookingFromDto.getEndDate())) {
            log.debug("Дата начала бронирования: {} позже даты окончания бронирования: {}.",
                    bookingFromDto.getStartDate(), bookingFromDto.getEndDate());
            throw new BookingBadRequestException(String.format("Дата начала бронирования: %tF %tT позже даты окончания " +
                            "бронирования: %tF %tT.", bookingFromDto.getStartDate(), bookingFromDto.getStartDate(),
                    bookingFromDto.getEndDate(), bookingFromDto.getEndDate()));
        }

        bookingFromDto.setBooker(booker);
        bookingFromDto.setStatus(BookingStatus.WAITING);

        Booking bookingForDto = bookingService.create(bookingFromDto);
        return BookingMapper.toDto(bookingForDto);
    }

    /*@GetMapping(value = { "/bookings", "/bookings/{bookingId}"})
    public Object getBookingS(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable(required = false) Long bookingId) throws UserNotFoundException {
        userService.getUserById(userId);

        if (bookingId == null) {
            return bookingService.getAllBookingsByUserId(userId).stream()
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toSet());
        } else {
            Booking bookingForDto = bookingService.getBookingById(bookingId);
            return BookingMapper.toDto(bookingForDto);
        }
    }*/

    /*@PatchMapping("/bookings/{bookingId}")
    public ResponseBookingDto putItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody RequestBookingDto requestBookingDto,
                           @PathVariable Long bookingId,
                           @RequestParam Boolean approved) {
        User user = userService.getUserById(userId);
        Booking booking = bookingService.getBookingById(bookingId);

        Booking bookingForDto = bookingService.update(booking);
        return BookingMapper.toDto(bookingForDto);
    }*/
}
