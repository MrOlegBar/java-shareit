package ru.practicum.shareit.server.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.booking.dto.BookingMapper;
import ru.practicum.shareit.server.booking.dto.BookingDtoForRequest;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.exception.BookingBadRequestException;
import ru.practicum.shareit.server.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.BookingState;
import ru.practicum.shareit.server.booking.model.BookingStatus;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserNotFoundException;
import ru.practicum.shareit.server.user.service.UserServiceImpl;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final UserServiceImpl userService;
    private final BookingService bookingService;
    private final ItemService itemService;
    private static final String DEFAULT_FROM_VALUE = "0";
    private static final String DEFAULT_SIZE_VALUE = "10";
    private static final String DEFAULT_STATE_VALUE = "ALL";
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping("/bookings")
    public BookingDto postBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                  @RequestBody BookingDtoForRequest bookingDtoForRequest)
            throws UserNotFoundException, BookingBadRequestException {

        Booking bookingFromDto = BookingMapper.toBooking(bookingDtoForRequest);
        Item item = itemService.getItemByIdOrElseThrow(bookingDtoForRequest.getItemId());
        bookingFromDto.setItem(item);

        if (userId.equals(bookingFromDto.getItem().getOwner().getId())) {
            log.debug("Невозможно забронировать свою вещь.");
            throw new BookingNotFoundException("Невозможно забронировать свою вещь.");
        }
        if (!bookingFromDto.getItem().getAvailable()) {
            log.debug("Вещь с itemId  = {} не доступна для бронирования.", bookingFromDto.getItem().getId());
            throw new BookingBadRequestException(String.format("Вещь с itemId = %s не доступна для бронирования.",
                    bookingFromDto.getItem().getId()));
        }
        if (bookingFromDto.getStartDate().isAfter(bookingFromDto.getEndDate()) ||
                bookingFromDto.getStartDate().equals(bookingFromDto.getEndDate()) ||
                bookingFromDto.getStartDate().isBefore(LocalDateTime.now())) {
            log.debug("Дата начала бронирования должна быть раньше даты окончания бронирования.");
            throw new BookingBadRequestException("Дата начала бронирования должна быть раньше " +
                    "даты окончания бронирования.");
        }
        User booker = userService.getUserByIdOrElseThrow(userId);

        bookingFromDto.setBooker(booker);
        bookingFromDto.setStatus(BookingStatus.WAITING);

        Booking bookingForDto = bookingService.create(bookingFromDto);
        return BookingMapper.toBookingDto(bookingForDto);
    }

    @PatchMapping("/bookings/{bookingId}")
    @Transactional
    public BookingDto patchBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam Boolean approved) throws BookingNotFoundException {

        userService.getUserByIdOrElseThrow(userId);

        Booking booking = bookingService.getBookingByIdOrElseThrow(bookingId);

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
        return BookingMapper.toBookingDto(bookingForDto);
    }

    @GetMapping({"/bookings", "/bookings/{bookingId}"})
    public Object getBookingS(@RequestHeader(USER_ID_HEADER) Long userId,
                              @RequestParam(required = false, defaultValue = DEFAULT_STATE_VALUE)
                              BookingState state,
                              @RequestParam(required = false, defaultValue = DEFAULT_FROM_VALUE) Integer from,
                              @RequestParam(required = false, defaultValue = DEFAULT_SIZE_VALUE) Integer size,
                              @PathVariable(required = false) Long bookingId)
            throws UserNotFoundException, BookingNotFoundException {

        userService.getUserByIdOrElseThrow(userId);

        if (bookingId == null) {
            return bookingService.getAllBookingsByBookerId(userId, state, from, size).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else {
            Booking bookingForDto = bookingService.getBookingByIdOrElseThrow(bookingId);

            if ((userId.equals(bookingForDto.getBooker().getId())) ||
                    (userId.equals(bookingForDto.getItem().getOwner().getId()))) {
                return BookingMapper.toBookingDto(bookingForDto);
            } else {
                log.debug("Получение данных о бронировании доступно автору бронирования или владельцу вещи.");
                throw new BookingNotFoundException("Получение данных о бронировании доступно автору бронирования или " +
                        "владельцу вещи.");
            }
        }
    }

    @GetMapping("/bookings/owner")
    public List<BookingDto> getBookingsOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @RequestParam(required = false, defaultValue = DEFAULT_STATE_VALUE)
                                             BookingState state,
                                             @RequestParam(required = false, defaultValue = DEFAULT_FROM_VALUE)
                                             Integer from,
                                             @RequestParam(required = false, defaultValue = DEFAULT_SIZE_VALUE)
                                             Integer size)
            throws UserNotFoundException, BookingNotFoundException {

        userService.getUserByIdOrElseThrow(userId);

        return bookingService.getAllBookingsByOwnerId(userId, state, from, size).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}