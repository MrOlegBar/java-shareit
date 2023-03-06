package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBooker_Id(Long bookerId, Sort startDateDescSort);

    Collection<Booking> findAllByBooker_IdAndStatus(Long bookerId, BookingStatus WAITING, Sort startDateDescSort);

    Collection<Booking> findAllByBooker_IdAndEndDateIsBefore(Long bookerId, LocalDateTime endDate,
                                                             Sort startDateDescSort);

    Collection<Booking> findAllByBooker_IdAndStartDateIsAfter(Long bookerId, LocalDateTime startDate,
                                                              Sort startDateDescSort);
    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND ?2 BETWEEN b.startDate AND b.endDate")
    Collection<Booking> findAllByBooker_IdAndStartDateIsBeforeAndEndDateIsAfter(Long bookerId, LocalDateTime dateTimeNow);

    Collection<Booking> findAllByItem_Owner_Id(Long ownerId, Sort sort);

    Collection<Booking> findAllByItem_Owner_IdAndStatus(Long ownerId, BookingStatus WAITING, Sort sort);

    Collection<Booking> findAllByItem_Owner_IdAndEndDateIsBefore(Long ownerId, LocalDateTime dateTimeNow, Sort sort);

    Collection<Booking> findAllByItem_Owner_IdAndStartDateIsAfter(Long ownerId, LocalDateTime dateTimeNow, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND ?2 BETWEEN b.startDate AND b.endDate")
    Collection<Booking> findAllByItem_Owner_IdAndStartDateIsBeforeAndEndDateIsAfter(Long ownerId,
                                                                                    LocalDateTime dateTimeNow);
    Optional<Booking> findFirstByItem_IdAndStatusAndEndDateIsBeforeOrderByEndDateAsc(Long itemId,
                                                                                     BookingStatus APPROVED,
                                                                                     LocalDateTime dateTimeNow);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartDateIsAfterOrderByStartDateAsc(Long itemId,
                                                                                        BookingStatus APPROVED,
                                                                                        LocalDateTime dateTimeNow);
}