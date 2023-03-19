package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
class BookingServiceTest {
    @Autowired
    private BookingService bookingService;
    @MockBean
    private BookingRepository bookingRepository;
    private BookingState state = BookingState.ALL;
    private final long userId = 1L;
    private final long bookingId = 1L;
    long wrongBookingId = 99L;
    private final int from = 0;
    private final int size = 10;
    User user = new User(
            1L,
            "user@user.com",
            "user");
    Item item = new Item(
            1L,
            "Дрель",
            "Простая дрель",
            true,
            user,
            null,
            new HashSet<>());
    private final Booking booking = new Booking(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            BookingStatus.WAITING,
            item,
            user);

    @Test
    public void shouldReturnCreatedBooking() {
        Mockito.when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        Booking actual = bookingService.create(booking);

        assertEquals(booking, actual);
    }

    @Test
    public void shouldReturnUpdatedBooking() {
        booking.setStatus(BookingStatus.APPROVED);

        Mockito.when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        Booking actual = bookingService.update(booking);

        assertEquals(booking, actual);
    }

    @Test
    public void shouldReturnBookingById() {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Booking actual = bookingService.getBookingById(bookingId);

        assertNotNull(actual);
        assertEquals(booking, actual);
    }

    @Test
    public void shouldReturnBookingNotFoundException() {
        Exception exception = assertThrows(BookingNotFoundException.class, () ->
                bookingService.getBookingById(wrongBookingId));

        String expectedMessage = String.format("Бронирование с bookingId = %s не найдено.", wrongBookingId);
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldReturnAllBookingByBookerId() {
        Mockito.when(bookingRepository.findAllByBooker_Id(anyLong(), any(Sort.class)))
                .thenReturn(List.of(booking));

        List<Booking> actual = bookingService.getAllBookingsByBookerId(bookingId);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnAllBookingByBookerIdWithPaginationAndStateAll() {
        Mockito.when(bookingRepository.findAllByBooker_Id(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<Booking> actual = bookingService.getAllBookingsByBookerId(userId, state, from, size);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnAllBookingByBookerIdWithPaginationAndStateWAITING() {
        state = BookingState.WAITING;

        Mockito.when(bookingRepository.findAllByBooker_IdAndStatus(anyLong(), any(BookingStatus.class),
                        any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<Booking> actual = bookingService.getAllBookingsByBookerId(userId, state, from, size);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnAllBookingByBookerIdWithPaginationAndStateREJECTED() {
        state = BookingState.REJECTED;

        Mockito.when(bookingRepository.findAllByBooker_IdAndStatus(anyLong(), any(BookingStatus.class),
                        any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<Booking> actual = bookingService.getAllBookingsByBookerId(userId, state, from, size);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnAllBookingByBookerIdWithPaginationAndStatePAST() {
        state = BookingState.PAST;

        Mockito.when(bookingRepository.findAllByBooker_IdAndEndDateIsBefore(anyLong(), any(LocalDateTime.class),
                        any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<Booking> actual = bookingService.getAllBookingsByBookerId(userId, state, from, size);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnAllBookingByBookerIdWithPaginationAndStateFUTURE() {
        state = BookingState.FUTURE;

        Mockito.when(bookingRepository.findAllByBooker_IdAndStartDateIsAfter(anyLong(), any(LocalDateTime.class),
                        any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<Booking> actual = bookingService.getAllBookingsByBookerId(userId, state, from, size);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnAllBookingByBookerIdWithPaginationAndStateCURRENT() {
        state = BookingState.CURRENT;

        Mockito.when(bookingRepository.findAllByBooker_IdAndDateTimeNowBetweenStartDateAndEndDate(anyLong(),
                        any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<Booking> actual = bookingService.getAllBookingsByBookerId(userId, state, from, size);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnAllBookingByOwnerIdWithPaginationAndStatusALL() {
        Mockito.when(bookingRepository.findAllByItem_Owner_Id(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<Booking> actual = bookingService.getAllBookingsByOwnerId(userId, state, from, size);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnAllBookingByOwnerIdWithPaginationAndStatusWAITING() {
        state = BookingState.WAITING;

        Mockito.when(bookingRepository.findAllByItem_Owner_IdAndStatus(anyLong(), any(BookingStatus.class),
                        any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<Booking> actual = bookingService.getAllBookingsByOwnerId(userId, state, from, size);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnAllBookingByOwnerIdWithPaginationAndStatusREJECTED() {
        state = BookingState.REJECTED;

        Mockito.when(bookingRepository.findAllByItem_Owner_IdAndStatus(anyLong(), any(BookingStatus.class),
                        any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<Booking> actual = bookingService.getAllBookingsByOwnerId(userId, state, from, size);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnAllBookingByOwnerIdWithPaginationAndStatusPAST() {
        state = BookingState.PAST;

        Mockito.when(bookingRepository.findAllByItem_Owner_IdAndEndDateIsBefore(anyLong(), any(LocalDateTime.class),
                        any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<Booking> actual = bookingService.getAllBookingsByOwnerId(userId, state, from, size);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnAllBookingByOwnerIdWithPaginationAndStatusFUTURE() {
        state = BookingState.FUTURE;

        Mockito.when(bookingRepository.findAllByItem_Owner_IdAndStartDateIsAfter(anyLong(), any(LocalDateTime.class),
                        any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<Booking> actual = bookingService.getAllBookingsByOwnerId(userId, state, from, size);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnAllBookingByOwnerIdWithPaginationAndStatusCURRENT() {
        state = BookingState.CURRENT;

        Mockito.when(bookingRepository.findAllByItem_Owner_IdAndDateTimeNowBetweenStartDateAndEndDate(anyLong(),
                        any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<Booking> actual = bookingService.getAllBookingsByOwnerId(userId, state, from, size);

        assertEquals(List.of(booking), actual);
    }
}