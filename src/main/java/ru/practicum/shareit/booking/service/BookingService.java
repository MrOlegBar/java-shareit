package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    Booking create(Booking booking);

    List<Booking> getAllBookingsByBookerId(long userId, BookingState state, int from, int size);

    List<Booking> getAllBookingsByBookerId(long bookerId);

    List<Booking> getAllBookingsByOwnerId(long ownerId, BookingState state, int from, int size);

    Booking getBookingById(long bookingId) throws BookingNotFoundException;

    Booking update(Booking booking);
}
