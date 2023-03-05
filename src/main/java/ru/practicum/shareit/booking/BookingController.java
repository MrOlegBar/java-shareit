package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.exception.BookingBadRequestException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constraintGroup.Post;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
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
        if (userId.equals(bookingFromDto.getItem().getOwner().getId())) {
            log.debug("Невозможно забронировать свою вещь.");
            throw new BookingNotFoundException("Невозможно забронировать свою вещь.");
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

    @GetMapping(value = { "/bookings", "/bookings/owner", "/bookings/{bookingId}"})
    public Object getBookingS(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable(required = false) Long bookingId,
                              @RequestParam(required = false, defaultValue = "ALL") BookingState state,
                              HttpServletRequest request)
            throws UserNotFoundException, BookingNotFoundException {

        userService.getUserById(userId);

        if (request.getServletPath().equals("/bookings")) {
            return bookingService.getAllBookingsByBookerId(userId, state).stream()
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toList());

        } else if (request.getServletPath().equals("/bookings/owner")){
            return bookingService.getAllBookingsByOwnerId(userId, state).stream()
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toList());

        }else {
            Booking bookingForDto = bookingService.getBookingById(bookingId);
            if ((userId.equals(bookingForDto.getBooker().getId())) ||
                    (userId.equals(bookingForDto.getItem().getOwner().getId()))) {
                return BookingMapper.toDto(bookingForDto);
            } else {
                log.debug("Получение данных о бронировании доступно автору бронирования или владельцу вещи.");
                throw new BookingNotFoundException("Получение данных о бронировании доступно автору бронирования или " +
                        "владельцу вещи.");
            }
        }
    }

    @PatchMapping("/bookings/{bookingId}")
    @Transactional
    public ResponseBookingDto putItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId,
                                      @RequestParam Boolean approved) {
        userService.getUserById(userId);
        Booking booking = bookingService.getBookingById(bookingId);

        if (!userId.equals(booking.getItem().getOwner().getId())) {
            log.debug("Подтверждение бронирования доступно только владельцу вещи.");
            throw new BookingNotFoundException("Подтверждение бронирования доступно только владельцу вещи.");
        }
        if (approved && booking.getStatus().equals(BookingStatus.APPROVED)) {
            log.debug("Статус бронирования уже подтвержден.");
            throw new BookingBadRequestException("Статус бронирования уже подтвержден.");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking bookingForDto = bookingService.update(booking);
        return BookingMapper.toDto(bookingForDto);
    }
}
