package ru.practicum.shareit.server.item.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.server.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    @Column(name = "comment_text")
    private String text;
    @ManyToOne
    @JoinColumn(name = "comment_author_id")
    private User author;
    @Column(name = "comment_created")
    @CreationTimestamp()
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss")
    private LocalDateTime created = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "comment_item_id")
    private Item item;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return (this.id != null && id.equals(comment.id));
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }
}