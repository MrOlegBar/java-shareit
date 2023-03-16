package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.base.BaseModel;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
public class Request extends BaseModel<Long> {
    @Column(name = "description")
    private String description;
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created = LocalDateTime.now();
    private User requester;
    private Set<Item> items = new HashSet<>();

    public Request(Long id, String description, User requester, Set<Item> items) {
        super(id);
        this.description = description;
        this.requester = requester;
        this.items = items;
    }

    public Request(String description, User requester, Set<Item> items) {
        this.description = description;
        this.requester = requester;
        this.items = items;
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
    @JoinColumn(name = "requester_id")
    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    @OneToMany(mappedBy = "request")
    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Request{" +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", created=" + created +
                ", requester=" + requester +
                ", items=" + items +
                '}';
    }
}