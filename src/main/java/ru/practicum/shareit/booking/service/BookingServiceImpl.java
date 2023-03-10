package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Service("BookingServiceImpl")
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    Sort startDateDescSort = Sort.by(Sort.Direction.DESC, "startDate");
    Collection<Booking> bookings = new ArrayList<>();

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Booking create(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Collection<Booking> getAllBookingsByBookerId(long bookerId, BookingState state) {
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_Id(bookerId, startDateDescSort);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(bookerId, BookingStatus.WAITING,
                        startDateDescSort);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(bookerId, BookingStatus.REJECTED,
                        startDateDescSort);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndEndDateIsBefore(bookerId, LocalDateTime.now(),
                        startDateDescSort);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStartDateIsAfter(bookerId, LocalDateTime.now(),
                        startDateDescSort);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBooker_IdAndDateTimeNowBetweenStartDateAndEndDate(bookerId, LocalDateTime.now());
                break;
        }
        return bookings;
    }

    @Override
    public Collection<Booking> getAllBookingsByOwnerId(long ownerId, BookingState state) {
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItem_Owner_Id(ownerId, startDateDescSort);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING,
                        startDateDescSort);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED,
                        startDateDescSort);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItem_Owner_IdAndEndDateIsBefore(ownerId, LocalDateTime.now(),
                        startDateDescSort);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStartDateIsAfter(ownerId, LocalDateTime.now(),
                        startDateDescSort);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItem_Owner_IdAndDateTimeNowBetweenStartDateAndEndDate(ownerId,
                        LocalDateTime.now());
                break;
        }
        return bookings;
    }

    @Override
    public Booking getBookingById(long bookingId) throws BookingNotFoundException {
        return bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.debug("???????????????????????? ?? bookingId  = {} ???? ??????????????.", bookingId);
            throw new BookingNotFoundException(String.format("???????????????????????? ?? bookingId = %s ???? ??????????????.",
                    bookingId));
        });
    }

    @Override
    public Booking update(Booking booking) {
        return bookingRepository.save(booking);
    }
}