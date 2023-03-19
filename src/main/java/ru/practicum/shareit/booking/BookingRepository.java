package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findAllByBooker_Id(Long bookerId, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStatus(Long bookerId, BookingStatus waiting, Pageable pageable);

    List<Booking> findAllByBooker_IdAndEndDateIsBefore(Long bookerId, LocalDateTime dateTimeNow, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStartDateIsAfter(Long bookerId, LocalDateTime dateTimeNow,
                                                        Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND ?2 BETWEEN b.startDate AND b.endDate " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByBooker_IdAndDateTimeNowBetweenStartDateAndEndDate(Long bookerId,
                                                                             LocalDateTime dateTimeNow,
                                                                             Pageable pageable);

    List<Booking> findAllByItem_Owner_Id(Long ownerId, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStatus(Long ownerId, BookingStatus waiting, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndEndDateIsBefore(Long ownerId, LocalDateTime dateTimeNow,
                                                           Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStartDateIsAfter(Long ownerId, LocalDateTime dateTimeNow,
                                                            Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND ?2 BETWEEN b.startDate AND b.endDate")
    List<Booking> findAllByItem_Owner_IdAndDateTimeNowBetweenStartDateAndEndDate(Long ownerId,
                                                                                 LocalDateTime dateTimeNow,
                                                                                 Pageable pageable);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartDateIsBeforeOrderByEndDateDesc(Long itemId,
                                                                                        BookingStatus approved,
                                                                                        LocalDateTime dateTimeNow);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartDateIsAfterOrderByStartDateAsc(Long itemId,
                                                                                        BookingStatus approved,
                                                                                        LocalDateTime dateTimeNow);
}