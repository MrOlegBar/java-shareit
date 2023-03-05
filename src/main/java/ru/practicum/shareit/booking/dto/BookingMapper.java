package ru.practicum.shareit.booking.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.service.ItemService;

@Component
public class BookingMapper {
    private static ItemService itemService;

    @Autowired
    public BookingMapper(@Qualifier("ItemServiceImpl")ItemService itemService) {
        BookingMapper.itemService = itemService;
    }

    public static ResponseBookingDto toDto(Booking booking) {
        return ResponseBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .build();
    }
    public static ResponseBookingForItemDto toDtoForItem(Booking booking) {
        return ResponseBookingForItemDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .status(booking.getStatus())
                .bookerId(booking.getBooker().getId())
                .item(booking.getItem())
                .build();
    }

    public static Booking toBooking(RequestBookingDto requestBookingDto) {
        Item item = itemService.getItemById(requestBookingDto.getItemId());

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStartDate(requestBookingDto.getStart());
        booking.setEndDate(requestBookingDto.getEnd());
        return booking;
    }
}