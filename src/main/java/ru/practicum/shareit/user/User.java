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
public class User extends BaseModel<Long> {
    @Column(name = "email")
    private String email;
    @Column(name = "name")
    private String name;

    public User(Long id, String email, String name) {
        super(id);
        this.email = email;
        this.name = name;
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

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name=" + name +
                '}';
    }
}