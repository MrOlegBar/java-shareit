package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql("/schema.sql")
class BookingRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private final LocalDateTime dateTime = LocalDateTime.now();
    private BookingStatus status = BookingStatus.WAITING;
    private final int from = 0;
    private final int size = 10;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "startDate");

    private final Pageable pageable = PageRequest.of(from, size, sort);
    private final long bookerId = 1L;
    private final User user = new User(
            "user@user.com",
            "user");
    private final Item item = new Item(
            "Дрель",
            "Простая дрель",
            true,
            user,
            null,
            new HashSet<>());
    private final Booking booking = new Booking(
            LocalDateTime.now().plusMonths(1),
            LocalDateTime.now().plusMonths(2),
            BookingStatus.WAITING,
            item,
            user);

    @BeforeEach
    void setUp() {
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @Test
    public void shouldReturnBookingsByBooker_Id() {
        List<Booking> actual = bookingRepository.findAllByBooker_Id(bookerId, sort);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnBookingsByBooker_IdWithPagination() {
        List<Booking> actual = bookingRepository.findAllByBooker_Id(bookerId, pageable);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnBookingsByBooker_IdAndStatusWithPagination() {
        List<Booking> actual = bookingRepository.findAllByBooker_IdAndStatus(bookerId, status, pageable);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnBookingsByBooker_IdAndEndDateWithPagination() {
        List<Booking> actual = bookingRepository.findAllByBooker_IdAndEndDateIsBefore(bookerId, dateTime, pageable);

        assertEquals(new ArrayList<>(), actual);
    }

    @Test
    public void shouldReturnBookingsByBooker_IdAndStartDateWithPagination() {
        List<Booking> actual = bookingRepository.findAllByBooker_IdAndStartDateIsAfter(bookerId, dateTime, pageable);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnBookingsByBooker_IdAndDateTimeNowBetweenStartAndEndDateWithPagination() {
        List<Booking> actual = bookingRepository.findAllByBooker_IdAndDateTimeNowBetweenStartDateAndEndDate(bookerId,
                dateTime, pageable);

        assertEquals(new ArrayList<>(), actual);
    }

    @Test
    public void shouldReturnBookingsByItem_Owner_IdWithPagination() {
        List<Booking> actual = bookingRepository.findAllByItem_Owner_Id(bookerId, pageable);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnBookingsByItem_Owner_IdAndStatusWithPagination() {
        List<Booking> actual = bookingRepository.findAllByItem_Owner_IdAndStatus(bookerId, status, pageable);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnBookingsByItem_Owner_IdAndEndDateWithPagination() {
        List<Booking> actual = bookingRepository.findAllByItem_Owner_IdAndEndDateIsBefore(bookerId, dateTime, pageable);

        assertEquals(new ArrayList<>(), actual);
    }

    @Test
    public void shouldReturnBookingsByItem_Owner_IdAndStartDateWithPagination() {
        List<Booking> actual = bookingRepository.findAllByItem_Owner_IdAndStartDateIsAfter(bookerId, dateTime, pageable);

        assertEquals(List.of(booking), actual);
    }

    @Test
    public void shouldReturnBookingsByItem_Owner_IdAndDateTimeNowBetweenStartAndEndDateWithPagination() {
        List<Booking> actual = bookingRepository.findAllByItem_Owner_IdAndDateTimeNowBetweenStartDateAndEndDate(bookerId,
                dateTime, pageable);

        assertEquals(new ArrayList<>(), actual);
    }

    @Test
    public void shouldReturnBookingByItem_IdAndStatusAndStartDateIsBefore() {
        status = BookingStatus.APPROVED;

        Optional<Booking> actual = bookingRepository.findFirstByItem_IdAndStatusAndStartDateIsBeforeOrderByEndDateDesc(
                bookerId, status, dateTime);

        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void shouldReturnBookingByItem_IdAndStatusAndStartDateIsAfter() {
        status = BookingStatus.APPROVED;

        Optional<Booking> actual = bookingRepository.findFirstByItem_IdAndStatusAndStartDateIsAfterOrderByStartDateAsc(
                bookerId, status, dateTime);

        assertEquals(Optional.empty(), actual);
    }
}
