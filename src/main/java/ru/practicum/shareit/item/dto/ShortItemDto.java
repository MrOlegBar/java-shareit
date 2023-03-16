package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ShortItemDto {
    private Long id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (this == o) {
            return true;
        } else if (!(o instanceof ShortItemDto)) {
            return false;
        } else {
            ShortItemDto shortItemDto = (ShortItemDto) o;
            if (!shortItemDto.getClass().equals(this.getClass())) {
                return false;
            } else {
                return this.id != null && this.id.equals(shortItemDto.id);
            }
        }
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }
}