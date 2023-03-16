package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
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
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
public class BookingServiceImplTest {
    @Autowired
    private BookingServiceImpl bookingService;
    @MockBean
    private BookingRepository bookingRepository;
    private Booking booking;
    private Booking testBooking;
    private List<Booking> testBookings;

    @BeforeEach
    public void setUp() {
        Request request = new Request();
        Set<Comment> comments = new HashSet<>();

        User user = new User(1L, "user@user.com", "user");
        Item item = new Item(1L, "Дрель", "Простая дрель", true, user, request, comments);
        booking = new Booking(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, item, user);
        testBooking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, item, user);

        testBookings = new ArrayList<>();
        testBookings.add(testBooking);
    }

    @Test
    public void shouldReturnCreatedBooking() {
        Mockito.when(bookingRepository.save(any(Booking.class)))
                .thenReturn(testBooking);

        Booking foundBooking = bookingService.create(booking);

        assertNotNull(foundBooking);
        assertEquals(testBooking, foundBooking);
    }

    @Test
    public void shouldReturnUpdatedBooking() {
        testBooking.setStatus(BookingStatus.APPROVED);

        Mockito.when(bookingRepository.save(any(Booking.class)))
                .thenReturn(testBooking);

        Booking foundBooking = bookingService.update(booking);

        assertNotNull(foundBooking);
        assertEquals(testBooking, foundBooking);
    }

    @Test
    public void shouldReturnBookingById() {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testBooking));

        Booking foundBooking = bookingService.getBookingById(1L);

        assertNotNull(foundBooking);
        assertEquals(testBooking, foundBooking);
    }

    @Test
    public void shouldReturnBookingNotFoundException() {
        Mockito.when(bookingRepository.findById(99L))
                .thenThrow(new BookingNotFoundException(String.format("Бронирование с bookingId = %s не найдено.",
                        99L)));

        Exception exception = assertThrows(BookingNotFoundException.class, () ->
                bookingService.getBookingById(99L));

        String expectedMessage = "Бронирование с bookingId = 99 не найдено.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void shouldReturnAllBookingByBookerId() {
        Mockito.when(bookingRepository.findAllByBooker_Id(anyLong(), any(Sort.class)))
                .thenReturn(testBookings);

        List<Booking> foundBookings = bookingService.getAllBookingsByBookerId(1L);

        assertNotNull(foundBookings);
        assertEquals(testBookings, foundBookings);
    }

    @Test
    public void shouldReturnAllBookingByBookerIdWithPagination() {
        Mockito.when(bookingRepository.findAllByBooker_Id(anyLong(), any(PageRequest.class)))
                .thenReturn(testBookings);

        List<Booking> foundBookings = bookingService.getAllBookingsByBookerId(1L, BookingState.ALL,
                0, 10);

        assertNotNull(foundBookings);
        assertEquals(testBookings, foundBookings);
    }

    @Test
    public void shouldReturnAllBookingByOwnerIdWithPagination() {
        Mockito.when(bookingRepository.findAllByItem_Owner_Id(anyLong(), any(PageRequest.class)))
                .thenReturn(testBookings);

        List<Booking> foundBookings = bookingService.getAllBookingsByOwnerId(1L, BookingState.ALL,
                0, 10);

        assertNotNull(foundBookings);
        assertEquals(testBookings, foundBookings);
    }
}
