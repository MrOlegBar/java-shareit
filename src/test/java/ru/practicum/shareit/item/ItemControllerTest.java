package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.exception.BookingBadRequestException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.MethodParametersException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.LessShortItemDto;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private RequestService requestService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private final String headerUserId = "X-Sharer-User-Id";
    private final String paramFrom = "from";
    private final String paramSize = "size";
    private final String size = "10";
    private final Long userId = 1L;
    private final Long itemId = 1L;
    private final Request request = new Request();
    private final User user = new User(
            1L,
            "user@user.com",
            "user");
    private final LessShortItemDto lessShortItemDto = LessShortItemDto.builder()
            .id(1L)
            .name("Дрель")
            .description("Простая дрель")
            .available(true)
            .requestId(1L)
            .build();
    private final Item item = ItemMapper.toItem(lessShortItemDto);
    private final ShortBookingDto lastBooking = ShortBookingDto.builder()
            .id(1L)
            .bookerId(1L)
            .build();
    private final ShortBookingDto nextBooking = ShortBookingDto.builder()
            .id(2L)
            .bookerId(1L)
            .build();
    private final CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("Add comment from user1")
            .authorName("user")
            .created(LocalDateTime.now())
            .build();
    private final Comment comment = CommentMapper.toComment(commentDto);
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Дрель")
            .description("Простая дрель")
            .available(true)
            .lastBooking(lastBooking)
            .nextBooking(nextBooking)
            .comments(Set.of(commentDto))
            .build();
    private final Booking booking = new Booking(
            1L,
            LocalDateTime.now().minusDays(2L),
            LocalDateTime.now().minusDays(1L),
            BookingStatus.APPROVED,
            item,
            user);
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @BeforeEach
    void setUp() {
        request.setId(1L);
        item.setRequest(request);
    }

    @Test
    void shouldReturnCreatedLessShortItemDto() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(itemService.create(any(Item.class)))
                .thenReturn(item);

        mockMvc.perform(post("/items")
                        .header(headerUserId, userId)
                        .content(mapper.writeValueAsString(lessShortItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(lessShortItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(lessShortItemDto.getName())))
                .andExpect(jsonPath("$.description", is(lessShortItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(lessShortItemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(lessShortItemDto.getRequestId()),
                        Long.class));
    }

    @Test
    void shouldReturnPatchedLessShortItemDto() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(itemService.getItemById(anyLong()))
                .thenReturn(item);
        when(requestService.getRequestById(anyLong()))
                .thenReturn(request);
        when(itemService.update(any(Item.class)))
                .thenReturn(item);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(headerUserId, userId)
                        .content(mapper.writeValueAsString(lessShortItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(lessShortItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(lessShortItemDto.getName())))
                .andExpect(jsonPath("$.description", is(lessShortItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(lessShortItemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(lessShortItemDto.getRequestId()),
                        Long.class));
    }

    @Test
    void shouldReturnItemDtoById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(itemService.getItemDtoByOwnerIdAndItemId(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(headerUserId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDto.getLastBooking().getId()),
                        Long.class))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemDto.getLastBooking()
                        .getBookerId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.id", is(itemDto.getNextBooking().getId()),
                        Long.class))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(itemDto.getNextBooking()
                        .getBookerId()), Long.class))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].text", is(commentDto.getText())))
                .andExpect(jsonPath("$.comments[0].authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.comments[0].created", is(commentDto.getCreated()
                        .format(formatter))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0"})
    void shouldReturnAllItemsDto(String from) throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(itemService.getAllItemsDtoByOwnerId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        ResultActions resultActions = mockMvc.perform(get("/items")
                .header(headerUserId, userId)
                .param(paramFrom, from)
                .param(paramSize, size));

        if (from.equals("-1")) {
            resultActions.andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodParametersException))
                    .andExpect(result -> assertEquals("Параметры запроса заданы не верно.",
                            Objects.requireNonNull(result.getResolvedException()).getMessage()));
        } else {
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                    .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                    .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                    .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                    .andExpect(jsonPath("$[0].lastBooking.id", is(itemDto.getLastBooking().getId()),
                            Long.class))
                    .andExpect(jsonPath("$[0].lastBooking.bookerId", is(itemDto.getLastBooking()
                            .getBookerId()), Long.class))
                    .andExpect(jsonPath("$[0].nextBooking.id", is(itemDto.getNextBooking().getId()),
                            Long.class))
                    .andExpect(jsonPath("$[0].nextBooking.bookerId", is(itemDto.getNextBooking()
                            .getBookerId()), Long.class))
                    .andExpect(jsonPath("$[0].comments", hasSize(1)))
                    .andExpect(jsonPath("$[0].comments[0].id", is(commentDto.getId()), Long.class))
                    .andExpect(jsonPath("$[0].comments[0].text", is(commentDto.getText())))
                    .andExpect(jsonPath("$[0].comments[0].authorName", is(commentDto.getAuthorName())))
                    .andExpect(jsonPath("$[0].comments[0].created", is(commentDto.getCreated()
                            .format(formatter))));

        }

    }

    @ParameterizedTest
    @CsvSource(value = {"-1:Дрель", "0:Дрель", "0:''"}, delimiter = ':')
    void shouldReturnAllLessShortItemDtoBySearch(String from, String text) throws Exception {
        when(itemService.findItemsBySearch(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        String paramText = "text";
        ResultActions resultActions = mockMvc.perform(get("/items/search")
                .param(paramText, text)
                .param(paramFrom, from)
                .param(paramSize, size));
        if (from.equals("-1")) {
            resultActions.andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodParametersException))
                    .andExpect(result -> assertEquals("Параметры запроса заданы не верно.",
                            Objects.requireNonNull(result.getResolvedException()).getMessage()));
        } else if (text.equals("")) {
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        } else {
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(lessShortItemDto.getId()), Long.class))
                    .andExpect(jsonPath("$[0].name", is(lessShortItemDto.getName())))
                    .andExpect(jsonPath("$[0].description", is(lessShortItemDto.getDescription())))
                    .andExpect(jsonPath("$[0].available", is(lessShortItemDto.getAvailable())))
                    .andExpect(jsonPath("$[0].requestId", is(lessShortItemDto.getRequestId()),
                            Long.class));
        }
    }

    @ParameterizedTest
    @ValueSource(longs = {2L, 1L})
    void shouldReturnCommentDto(long itemId) throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(itemService.getItemById(anyLong()))
                .thenReturn(item);
        when(bookingService.getAllBookingsByBookerId(anyLong()))
                .thenReturn(List.of(booking));
        when(itemService.createComment(any(Comment.class)))
                .thenReturn(comment);

        ResultActions resultActions = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                .header(headerUserId, userId)
                .content(mapper.writeValueAsString(commentDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        if (itemId == 2) {
            resultActions.andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingBadRequestException))
                    .andExpect(result -> assertEquals(String.format("Вещь с itemId = %s не найдена в истории бронирования " +
                                    "пользователя с userId %s.", 2L, userId),
                            Objects.requireNonNull(result.getResolvedException()).getMessage()));
        } else {
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                    .andExpect(jsonPath("$.text", is(commentDto.getText())))
                    .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                    .andExpect(jsonPath("$.created", is(commentDto.getCreated().format(formatter))));
        }
    }
}