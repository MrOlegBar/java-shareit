package ru.practicum.shareit.item.model;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.base.BaseModel;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@ToString
public class Comment extends BaseModel<Long> {
    @Column(name = "text")
    private String text;
    private User author;
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created = LocalDateTime.now();
    private Item item;

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
    @JoinColumn(name = "authorName_id")
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User authorName) {
        this.author = authorName;
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