package ru.practicum.shareit.server.booking.model;

import lombok.*;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;
    @Column(name = "booking_start_date")
    private LocalDateTime startDate;
    @Column(name = "booking_end_date")
    private LocalDateTime endDate;
    @Column(name = "booking_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    @ManyToOne
    @JoinColumn(name = "booking_item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booking_booker_id")
    private User booker;

    public Booking(LocalDateTime startDate, LocalDateTime endDate, BookingStatus status, Item item, User booker) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.item = item;
        this.booker = booker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return (this.id != null && id.equals(booking.id));
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }
}