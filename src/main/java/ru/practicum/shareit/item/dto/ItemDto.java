package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.constraintGroup.Post;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Builder
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Название вещи отсутствует или представлено пустым символом.", groups = Post.class)
    private String name;
    @NotNull(message = "Описание вещи отсутствует.", groups = Post.class)
    private String description;
    @NotNull(message = "Статус доступности вещи отсутствует.", groups = Post.class)
    private Boolean available;
    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;
    private Set<CommentDto> comments;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDto itemDto = (ItemDto) o;
        return id.equals(itemDto.id);
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public ShortBookingDto getLastBooking() {
        return lastBooking;
    }

    public void setLastBooking(ShortBookingDto lastBooking) {
        this.lastBooking = lastBooking;
    }

    public ShortBookingDto getNextBooking() {
        return nextBooking;
    }

    public void setNextBooking(ShortBookingDto nextBooking) {
        this.nextBooking = nextBooking;
    }

    public Set<CommentDto> getComments() {
        return comments;
    }

    public void setComments(Set<CommentDto> comments) {
        this.comments = comments;
    }
}