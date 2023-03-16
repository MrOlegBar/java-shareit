package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ShortBookingDto {
    private Long id;
    private Long bookerId;

    @Override
    public String toString() {
        return "ShortBookingDto{" +
                "id=" + id +
                ", bookerId=" + bookerId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (this == o) {
            return true;
        } else if (!(o instanceof ShortBookingDto)) {
            return false;
        } else {
            ShortBookingDto bookingDto = (ShortBookingDto) o;
            if (!bookingDto.getClass().equals(this.getClass())) {
                return false;
            } else {
                return this.id != null && this.id.equals(bookingDto.id);
            }
        }
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }
}