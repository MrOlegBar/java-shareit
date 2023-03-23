package ru.practicum.shareit.server.request;

import lombok.*;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;
    @Column(name = "request_description")
    private String description;
    @Column(name = "request_created", nullable = false, updatable = false)
    private LocalDateTime created = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "request_requester_id")
    private User requester;
    @OneToMany(mappedBy = "request")
    private Set<Item> items = new HashSet<>();

    public Request(Long id, String description, User requester, Set<Item> items) {
        this.id = id;
        this.description = description;
        this.requester = requester;
        this.items = items;
    }

    public Request(String description, User requester, Set<Item> items) {
        this.description = description;
        this.requester = requester;
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return (this.id != null && id.equals(request.id));
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }
}