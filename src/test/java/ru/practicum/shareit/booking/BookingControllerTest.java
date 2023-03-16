package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForRequest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.exception.BookingBadRequestException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.LessShortItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private final BookingDtoForRequest bookingDtoForRequest = BookingDtoForRequest.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();
    private final LessShortItemDto lessShortItemDtoRequest = LessShortItemDto.builder()
            .name("Дрель")
            .description("Простая дрель")
            .available(true)
            .requestId(1L)
            .build();
    private final User user = new User(
            1L,
            "user@user.com",
            "user");
    private final Request request = new Request();
    private Item itemForDto;
    private Booking bookingForDto;
    private final List<Booking> bookings = new ArrayList<>();
    private BookingDto bookingDtoForResponse;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @BeforeEach
    void setUp() {
        request.setId(1L);

        itemForDto = ItemMapper.toItem(lessShortItemDtoRequest);
        itemForDto.setId(1L);
        itemForDto.setAvailable(true);
        itemForDto.setOwner(user);
        itemForDto.setRequest(request);

        bookingForDto = BookingMapper.toBooking(bookingDtoForRequest);
        bookingForDto.setItem(itemForDto);
        bookingForDto.setBooker(user);
        bookingForDto.getBooker().setId(2L);
        bookingForDto.setStatus(BookingStatus.WAITING);

        bookings.add(bookingForDto);

        bookingDtoForResponse = BookingMapper.toBookingDto(bookingForDto);
    }

    @Test
    void shouldReturnCreatedBookingDto() throws Exception {
        when(itemService.getItemById(anyLong()))
                .thenReturn(itemForDto);
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(bookingService.create(any(Booking.class)))
                .thenReturn(bookingForDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDtoForRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoForResponse.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoForResponse.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(bookingDtoForResponse.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoForResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDtoForResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoForResponse.getItem().getName())));
    }

    @Test
    void shouldReturnBookingBadRequestException() throws Exception {
        itemForDto.setAvailable(false);

        when(itemService.getItemById(anyLong()))
                .thenReturn(itemForDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDtoForRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingBadRequestException))
                .andExpect(result -> assertEquals("Вещь с itemId = 1 не доступна для бронирования.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void shouldReturnPatchedBookingDto() throws Exception {
        bookingDtoForResponse.setStatus(BookingStatus.APPROVED);

        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(bookingService.getBookingById(anyLong()))
                .thenReturn(bookingForDto);
        when(bookingService.update(any(Booking.class)))
                .thenReturn(bookingForDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 2L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoForResponse.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoForResponse.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(bookingDtoForResponse.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoForResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDtoForResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoForResponse.getItem().getName())));
    }

    @Test
    void shouldReturnBookingDtoById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(bookingService.getBookingById(anyLong()))
                .thenReturn(bookingForDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoForResponse.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoForResponse.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(bookingDtoForResponse.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoForResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDtoForResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoForResponse.getItem().getName())));
    }

    @Test
    void shouldReturnAllBookingDto() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(bookingService.getAllBookingsByBookerId(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDtoForResponse.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingDtoForResponse.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(bookingDtoForResponse.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoForResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoForResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDtoForResponse.getItem().getName())));
    }

    @Test
    void shouldReturnAllBookingDtoForOwner() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(bookingService.getAllBookingsByOwnerId(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDtoForResponse.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingDtoForResponse.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(bookingDtoForResponse.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoForResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoForResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDtoForResponse.getItem().getName())));
    }
}
