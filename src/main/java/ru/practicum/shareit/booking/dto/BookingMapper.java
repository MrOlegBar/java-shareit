package ru.practicum.shareit.booking.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;

@Component
public class BookingMapper {
    private static ItemService itemService;

    @Autowired
    public BookingMapper(@Qualifier("ItemServiceImpl") ItemService itemService) {
        BookingMapper.itemService = itemService;
    }

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
        Item item = itemService.getItemById(bookingDtoForRequest.getItemId());

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStartDate(bookingDtoForRequest.getStart());
        booking.setEndDate(bookingDtoForRequest.getEnd());
        return booking;
    }
}