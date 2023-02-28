package ru.practicum.shareit.user;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
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
    private Set<Item> items;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "user")
    @Fetch(FetchMode.JOIN)
    public Set<Item> getItems() {
        return items;
    }
}