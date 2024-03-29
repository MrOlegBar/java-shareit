package ru.practicum.shareit.server.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.BookingState;
import ru.practicum.shareit.server.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("BookingServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    Sort startDateDescSort = Sort.by(Sort.Direction.DESC, "startDate");
    List<Booking> bookings = new ArrayList<>();

    @Override
    public Booking create(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Booking update(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingByIdOrElseThrow(long bookingId) throws BookingNotFoundException {
        return bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.debug("Бронирование с bookingId  = {} не найдено.", bookingId);
            throw new BookingNotFoundException(String.format("Бронирование с bookingId = %s не найдено.",
                    bookingId));
        });
    }

    @Override
    public List<Booking> getAllBookingsByBookerId(long bookerId) {
        return bookingRepository.findAllByBooker_Id(bookerId, startDateDescSort);
    }

    @Override
    public List<Booking> getAllBookingsByBookerId(long bookerId, BookingState state, int from, int size) {
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_Id(bookerId,
                        PageRequest.of(from / size, size, startDateDescSort));
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(bookerId, BookingStatus.WAITING,
                        PageRequest.of(from, size, startDateDescSort));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(bookerId, BookingStatus.REJECTED,
                        PageRequest.of(from, size, startDateDescSort));
                break;
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndEndDateIsBefore(bookerId, LocalDateTime.now(),
                        PageRequest.of(from, size, startDateDescSort));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStartDateIsAfter(bookerId, LocalDateTime.now(),
                        PageRequest.of(from, size, startDateDescSort));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBooker_IdAndDateTimeNowBetweenStartDateAndEndDate(bookerId,
                        LocalDateTime.now(), PageRequest.of(from, size, startDateDescSort));
                break;
        }
        return bookings;
    }

    @Override
    public List<Booking> getAllBookingsByOwnerId(long ownerId, BookingState state, int from, int size) {
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItem_Owner_Id(ownerId,
                        PageRequest.of(from, size, startDateDescSort));
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING,
                        PageRequest.of(from, size, startDateDescSort));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED,
                        PageRequest.of(from, size, startDateDescSort));
                break;
            case PAST:
                bookings = bookingRepository.findAllByItem_Owner_IdAndEndDateIsBefore(ownerId, LocalDateTime.now(),
                        PageRequest.of(from, size, startDateDescSort));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStartDateIsAfter(ownerId, LocalDateTime.now(),
                        PageRequest.of(from, size, startDateDescSort));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItem_Owner_IdAndDateTimeNowBetweenStartDateAndEndDate(ownerId,
                        LocalDateTime.now(), PageRequest.of(from, size, startDateDescSort));
                break;
        }
        return bookings;
    }
}