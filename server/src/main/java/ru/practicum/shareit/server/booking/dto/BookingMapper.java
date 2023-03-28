package ru.practicum.shareit.server.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.server.user.dto.UserMapper;

@Component
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .status(booking.getStatus())
                .booker(UserMapper.toShortUserDto(booking.getBooker()))
                .item(ItemMapper.toShortItemDto(booking.getItem()))
                .build();
    }

    public static ShortBookingDto toShortBookingDto(Booking booking) {
        return ShortBookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static Booking toBooking(BookingDtoForRequest bookingDtoForRequest) {
        Booking booking = new Booking();
        booking.setStartDate(bookingDtoForRequest.getStart());
        booking.setEndDate(bookingDtoForRequest.getEnd());
        return booking;
    }
}