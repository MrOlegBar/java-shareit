package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

@Service("BookingServiceImpl")
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Booking create(Booking booking) {
        return bookingRepository.save(booking);
    }

    /*@Override
    public Collection<Booking> getAllBookingsByUserId(long userId) {
        return bookingRepository.findAllByBooker_Id(userId);
    }

    @Override
    public Booking getBookingById(long bookingId) throws BookingNotFoundException {
        return bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.debug("Бронирование с bookingId  = {} не найдено.", bookingId);
            throw new BookingNotFoundException(String.format("Бронирование с bookingId = %s не найдено.",
                    bookingId));
        });
    }

    @Override
    public Booking update(Booking booking) {
        return bookingRepository.save(booking);
    }*/
}