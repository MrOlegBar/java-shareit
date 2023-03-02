package ru.practicum.shareit.user;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.base.BaseModel;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users",
        uniqueConstraints = @UniqueConstraint(name = "unique_email", columnNames = "email"))
@NoArgsConstructor
@Setter @Getter @ToString
public class User extends BaseModel<Long> {
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    //private Set<Item> items;
    //private Set<Booking> bookings;

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

    /*@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    public Set<Item> getItems() {
        return items;
    }*/

    /*@OneToMany(mappedBy = "booker", fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }*/
}