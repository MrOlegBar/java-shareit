package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {
    Booking create(Booking booking);

    Collection<Booking> getAllBookingsByBookerId(long userId, BookingState state);

    Collection<Booking> getAllBookingsByOwnerId(long userId, BookingState state);

    Booking getBookingById(long bookingId) throws BookingNotFoundException;

    Booking update(Booking booking);
}
