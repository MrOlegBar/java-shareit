package ru.practicum.shareit.server.item.model;

import lombok.*;
import ru.practicum.shareit.server.request.Request;
import ru.practicum.shareit.server.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;
    @Column(name = "item_name")
    private String name;
    @Column(name = "item_description")
    private String description;
    @Column(name = "item_is_available")
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "item_owner_id")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "item_request_id")
    private Request request;
    @OneToMany(mappedBy = "item")
    private Set<Comment> comments = new HashSet<>();

    public Item(String name, String description, Boolean available, User owner, Request request, Set<Comment> comments) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return (this.id != null && id.equals(item.id));
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }
}