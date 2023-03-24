package ru.practicum.shareit.gateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.booking.exception.BookingBadRequestException;
import ru.practicum.shareit.gateway.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.gateway.constraintGroup.Post;
import ru.practicum.shareit.gateway.user.UserClient;
import ru.practicum.shareit.gateway.user.UserNotFoundException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final UserClient userClient;
    private final BookingClient bookingClient;
    private static final String DEFAULT_FROM_VALUE = "0";
    private static final String DEFAULT_SIZE_VALUE = "10";
    private static final String DEFAULT_STATE_VALUE = "ALL";
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping("/bookings")
    public ResponseEntity<Object> postBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @Validated(Post.class) @RequestBody BookingDtoForRequest bookingDtoForRequest)
            throws UserNotFoundException, BookingBadRequestException {

        return bookingClient.postBooking(userId, bookingDtoForRequest);
    }

    @PatchMapping("/bookings/{bookingId}")
    public ResponseEntity<Object> patchBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                               @PathVariable Long bookingId,
                                               @RequestParam Boolean approved) throws UserNotFoundException,
            BookingNotFoundException {

        return bookingClient.patchBooking(bookingId, approved, userId);
    }

    @GetMapping({"/bookings", "/bookings/{bookingId}"})
    public ResponseEntity<Object> getBookingS(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @RequestParam(required = false, defaultValue = DEFAULT_STATE_VALUE)
                                              BookingState state,
                                              @RequestParam(required = false, defaultValue = DEFAULT_FROM_VALUE)
                                              @PositiveOrZero Integer from,
                                              @RequestParam(required = false, defaultValue = DEFAULT_SIZE_VALUE)
                                              @Positive Integer size,
                                              @PathVariable(required = false) Long bookingId)
            throws UserNotFoundException, BookingNotFoundException {

        /*if (from < 0 || size <= 0) {
            log.debug("Параметры запроса заданы не верно.");
            throw new MethodParametersException("Параметры запроса заданы не верно.");
        }*/

        if (bookingId == null) {
            return bookingClient.getBookings(userId, state, from, size);
        } else {
            return bookingClient.getBooking(userId, bookingId);
        }
    }

    @GetMapping("/bookings/owner")
    public ResponseEntity<Object> getBookingsOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                                   @RequestParam(required = false,
                                                           defaultValue = DEFAULT_STATE_VALUE) BookingState state,
                                                   @RequestParam(required = false, defaultValue = DEFAULT_FROM_VALUE)
                                                   @PositiveOrZero Integer from,
                                                   @RequestParam(required = false, defaultValue = DEFAULT_SIZE_VALUE)
                                                   @Positive Integer size)
            throws UserNotFoundException, BookingNotFoundException {

        /*if (from < 0 || size <= 0) {
            log.debug("Параметры запроса заданы не верно.");
            throw new MethodParametersException("Параметры запроса заданы не верно.");
        }*/

        return bookingClient.getBookingsOwner(state, userId, from, size);
    }
}