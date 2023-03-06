package ru.practicum.shareit.user;

import lombok.*;
import ru.practicum.shareit.base.BaseModel;

import javax.persistence.*;

@Entity
@Table(name = "users",
        uniqueConstraints = @UniqueConstraint(name = "unique_email", columnNames = "email"))
@NoArgsConstructor
@Setter
@Getter
@ToString
public class User extends BaseModel<Long> {
    @Column(name = "email")
    private String email;
    @Column(name = "name")
    private String name;

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
}