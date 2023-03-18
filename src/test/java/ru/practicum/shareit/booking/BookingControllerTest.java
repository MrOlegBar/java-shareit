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
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.MethodParametersException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
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
    private final String headerUserId = "X-Sharer-User-Id";
    private final String paramApproved = "approved";
    private final String paramState = "state";
    private final String paramFrom = "from";
    private final String paramSize = "size";
    private final String wrongFrom = "-1";
    private final String from = "0";
    private final String size = "10";
    private String approved = "true";
    private final String state = "ALL";
    private final Long userId = 1L;
    private final Long itemId = 1L;
    private final Long bookingId = 1L;
    private final User user = new User(
            1L,
            "user@user.com",
            "user");
    private final Request request = new Request();
    private final Item item = new Item(
            1L,
            "Дрель",
            "Простая дрель",
            true,
            user,
            request,
            new HashSet<>());
    private final BookingDtoForRequest bookingDtoForRequest = BookingDtoForRequest.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();
    private final Booking booking = BookingMapper.toBooking(bookingDtoForRequest);
    private BookingDto bookingDto;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @BeforeEach
    void setUp() {
        request.setId(1L);

        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        bookingDto = BookingMapper.toBookingDto(booking);
    }

    @Test
    void shouldReturnCreatedBookingDto() throws Exception {
        booking.getBooker().setId(2L);
        bookingDto = BookingMapper.toBookingDto(booking);

        when(itemService.getItemById(anyLong()))
                .thenReturn(item);
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(bookingService.create(any(Booking.class)))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header(headerUserId, userId)
                        .content(mapper.writeValueAsString(bookingDtoForRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())));
    }

    @Test
    void shouldReturnBookingNotFoundExceptionForPostBooking() throws Exception {
        when(itemService.getItemById(anyLong()))
                .thenReturn(item);

        mockMvc.perform(post("/bookings")
                        .header(headerUserId, userId)
                        .content(mapper.writeValueAsString(bookingDtoForRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingNotFoundException))
                .andExpect(result -> assertEquals("Невозможно забронировать свою вещь.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void shouldReturnBookingBadRequestExceptionForAvailableFalse() throws Exception {
        item.setAvailable(false);
        booking.getBooker().setId(2L);
        bookingDto = BookingMapper.toBookingDto(booking);

        when(itemService.getItemById(anyLong()))
                .thenReturn(item);

        mockMvc.perform(post("/bookings")
                        .header(headerUserId, userId)
                        .content(mapper.writeValueAsString(bookingDtoForRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingBadRequestException))
                .andExpect(result -> assertEquals(String.format("Вещь с itemId = %s не доступна для бронирования.",
                        itemId), Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void shouldReturnBookingBadRequestExceptionForFailStarAndEndDate() throws Exception {
        bookingDtoForRequest.setStart(bookingDtoForRequest.getEnd());
        booking.getBooker().setId(2L);
        bookingDto = BookingMapper.toBookingDto(booking);

        when(itemService.getItemById(anyLong()))
                .thenReturn(item);

        mockMvc.perform(post("/bookings")
                        .header(headerUserId, userId)
                        .content(mapper.writeValueAsString(bookingDtoForRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingBadRequestException))
                .andExpect(result -> assertEquals("Дата начала бронирования должна быть раньше даты окончания " +
                        "бронирования.", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void shouldReturnPatchedBookingDtoWithApprovedTrue() throws Exception {
        bookingDto.setStatus(BookingStatus.APPROVED);

        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(bookingService.getBookingById(anyLong()))
                .thenReturn(booking);
        when(bookingService.update(any(Booking.class)))
                .thenReturn(booking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(headerUserId, userId)
                        .param(paramApproved, approved)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())));
    }

    @Test
    void shouldReturnPatchedBookingDtoWithApprovedFalse() throws Exception {
        approved = "false";
        bookingDto.setStatus(BookingStatus.REJECTED);

        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(bookingService.getBookingById(anyLong()))
                .thenReturn(booking);
        when(bookingService.update(any(Booking.class)))
                .thenReturn(booking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(headerUserId, userId)
                        .param(paramApproved, approved)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())));
    }

    @Test
    void shouldReturnBookingNotFoundExceptionForPatchBooking() throws Exception {
        booking.getBooker().setId(2L);
        bookingDto = BookingMapper.toBookingDto(booking);

        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(bookingService.getBookingById(anyLong()))
                .thenReturn(booking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(headerUserId, userId)
                        .param(paramApproved, approved)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingNotFoundException))
                .andExpect(result -> assertEquals("Подтверждение бронирования доступно только владельцу вещи.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void shouldReturnBookingBadRequestExceptionForStatusApproved() throws Exception {
        booking.setStatus(BookingStatus.APPROVED);

        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(bookingService.getBookingById(anyLong()))
                .thenReturn(booking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(headerUserId, userId)
                        .param(paramApproved, approved)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingBadRequestException))
                .andExpect(result -> assertEquals("Статус бронирования уже подтвержден.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void shouldReturnBookingDtoById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(bookingService.getBookingById(anyLong()))
                .thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(headerUserId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())));
    }

    @Test
    void shouldReturnBookingNotFoundExceptionForGetBookingById() throws Exception {
        booking.getBooker().setId(2L);
        bookingDto = BookingMapper.toBookingDto(booking);

        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(bookingService.getBookingById(anyLong()))
                .thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(headerUserId, userId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingNotFoundException))
                .andExpect(result -> assertEquals("Получение данных о бронировании доступно автору бронирования " +
                                "или владельцу вещи.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void shouldReturnAllBookingDto() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(bookingService.getAllBookingsByBookerId(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings")
                        .header(headerUserId, userId)
                        .param(paramState, state)
                        .param(paramFrom, from)
                        .param(paramSize, size))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName())));
    }

    @Test
    void shouldReturnMethodParametersExceptionForGetBookings() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);

        mockMvc.perform(get("/bookings")
                        .header(headerUserId, userId)
                        .param(paramState, state)
                        .param(paramFrom, wrongFrom)
                        .param(paramSize, size))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodParametersException))
                .andExpect(result -> assertEquals("Параметры запроса заданы не верно.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void shouldReturnAllBookingDtoForOwner() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(bookingService.getAllBookingsByOwnerId(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/owner")
                        .header(headerUserId, userId)
                        .param(paramState, state)
                        .param(paramFrom, from)
                        .param(paramSize, size))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName())));
    }

    @Test
    void shouldReturnMethodParametersExceptionForGetBookingsOwner() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);

        mockMvc.perform(get("/bookings/owner")
                        .header(headerUserId, userId)
                        .param(paramState, state)
                        .param(paramFrom, wrongFrom)
                        .param(paramSize, size))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodParametersException))
                .andExpect(result -> assertEquals("Параметры запроса заданы не верно.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}
