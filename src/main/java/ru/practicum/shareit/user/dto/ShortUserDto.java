package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ShortUserDto {
    private Long id;

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (this == o) {
            return true;
        } else if (!(o instanceof ShortUserDto)) {
            return false;
        } else {
            ShortUserDto shortUserDto = (ShortUserDto) o;
            if (!shortUserDto.getClass().equals(this.getClass())) {
                return false;
            } else {
                return this.id != null && this.id.equals(shortUserDto.id);
            }
        }
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }
}