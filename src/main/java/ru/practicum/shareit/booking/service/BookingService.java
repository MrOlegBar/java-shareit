package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {
    Booking create(Booking booking);

    /*Collection<Booking> getAllBookingsByUserId(long userId);

    Booking getBookingById(long itemId) throws BookingNotFoundException;

    Booking update(Booking booking);*/
}
