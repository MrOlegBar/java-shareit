package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.base.BaseModel;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends BaseModel<Long> {
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    private Item item;
    private User booker;

    public Booking(Long id, LocalDateTime startDate, LocalDateTime endDate, BookingStatus status, Item item,
                   User booker) {
        super(id);
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.item = item;
        this.booker = booker;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "booker_id")
    public User getBooker() {
        return booker;
    }

    public void setBooker(User booker) {
        this.booker = booker;
    }

    @ManyToOne
    @JoinColumn(name = "item_id")
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}