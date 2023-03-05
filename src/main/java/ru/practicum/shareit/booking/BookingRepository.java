package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBooker_Id(Long bookerId, Sort startDateDescSort);

    Collection<Booking> findAllByBooker_IdAndStatus(Long bookerId, BookingStatus waiting, Sort startDateDescSort);

    Collection<Booking> findAllByBooker_IdAndEndDateIsBefore(Long bookerId, LocalDateTime endDate,
                                                             Sort startDateDescSort);

    Collection<Booking> findAllByBooker_IdAndStartDateIsAfter(Long bookerId, LocalDateTime startDate,
                                                              Sort startDateDescSort);

    Collection<Booking> findAllByBooker_IdAndStartDateIsBeforeAndEndDateIsAfter(Long bookerId,
                                                                                LocalDateTime dateTimeNow,
                                                                                LocalDateTime nowDateTime,
                                                                                Sort startDateDescSort);

    Collection<Booking> findAllByItem_Owner_Id(Long ownerId, Sort sort);

    Collection<Booking> findAllByItem_Owner_IdAndStatus(Long ownerId, BookingStatus waiting, Sort sort);

    Collection<Booking> findAllByItem_Owner_IdAndEndDateIsBefore(Long ownerId, LocalDateTime now, Sort sort);

    Collection<Booking> findAllByItem_Owner_IdAndStartDateIsAfter(Long ownerId, LocalDateTime now, Sort sort);

    Collection<Booking> findAllByItem_Owner_IdAndStartDateIsBeforeAndEndDateIsAfter(Long ownerId, LocalDateTime now,
                                                                                    LocalDateTime now1, Sort sort);
    Optional<Booking> findLastByItem_IdAndStatusAndEndDateIsBefore(Long itemId, BookingStatus APPROVED,
                                                                   LocalDateTime now);

    Optional<Booking> findFirstByItem_IdAndStatusAndEndDateIsAfter(Long itemId, BookingStatus APPROVED,
                                                                   LocalDateTime now);

    /*Optional<Booking> findLastByItem_IdAndEndIsBefore(Long item, LocalDateTime end);

    Optional<Booking> findFirstByItem_IdAndEndIsAfter(Long item, LocalDateTime start);*/
}