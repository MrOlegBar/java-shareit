package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.base.BaseModel;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Getter @Setter @ToString
@NoArgsConstructor
public class Item extends BaseModel<Long> {
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "available")
    private Boolean available;
    private User owner;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name="user_id")
    public User getOwner() {
        return owner;
    }
}