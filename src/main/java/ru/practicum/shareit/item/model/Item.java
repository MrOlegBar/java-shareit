package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.base.BaseModel;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Item extends BaseModel<Long> {
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "available")
    private Boolean available;
    private User owner;
    private Set<Comment> comments = new HashSet<>();
    private Request request;

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
    @JoinColumn(name = "owner_id")
    public User getOwner() {
        return owner;
    }

    @OneToMany(mappedBy = "item")
    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @ManyToOne
    @JoinColumn(name = "request_id")
    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}