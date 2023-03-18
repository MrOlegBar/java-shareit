package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.MethodParametersException;
import ru.practicum.shareit.item.dto.LessShortItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoForRequest;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
public class RequestControllerTest {
    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private RequestService requestService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private final String headerUserId = "X-Sharer-User-Id";
    private final Long userId = 1L;
    String paramFrom = "from";
    String paramSize = "size";
    String from = "0";
    String size = "10";
    private final RequestDtoForRequest requestDtoForRequest = RequestDtoForRequest.builder()
            .description("Хотел бы воспользоваться щёткой для обуви")
            .build();
    private final User user = new User(
            1L,
            "user@user.com",
            "user");
    private final Item item = new Item(
            1L,
            "Дрель",
            "Простая дрель",
            true,
            user,
            null,
            new HashSet<>());
    private final Request request = new Request(
            1L,
            "Хотел бы воспользоваться щёткой для обуви",
            user,
            Set.of(item));
    private final RequestDto requestDto = RequestMapper.toRequestDto(request);
    private final LessShortItemDto lessShortItemDto = ItemMapper.toLessShortItemDto(item);
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void shouldReturnCreatedRequestDto() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(requestService.create(any(Request.class)))
                .thenReturn(request);

        mockMvc.perform(post("/requests")
                        .header(headerUserId, userId)
                        .content(mapper.writeValueAsString(requestDtoForRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().format(formatter))))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(lessShortItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(lessShortItemDto.getName())))
                .andExpect(jsonPath("$.items[0].description", is(lessShortItemDto.getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(lessShortItemDto.getAvailable())))
                .andExpect(jsonPath("$.items[0].requestId", is(lessShortItemDto.getRequestId()), Long.class));
    }

    @Test
    void shouldReturnRequestDtoById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(requestService.getRequestById(anyLong()))
                .thenReturn(request);

        Long requestId = 1L;
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(headerUserId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().format(formatter))))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(lessShortItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(lessShortItemDto.getName())))
                .andExpect(jsonPath("$.items[0].description", is(lessShortItemDto.getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(lessShortItemDto.getAvailable())))
                .andExpect(jsonPath("$.items[0].requestId", is(lessShortItemDto.getRequestId()), Long
                        .class));
    }

    @Test
    void shouldReturnAllRequestDto() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(requestService.getAllRequestsByUserId(anyLong()))
                .thenReturn(List.of(request));

        mockMvc.perform(get("/requests")
                        .header(headerUserId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(requestDto.getCreated().format(formatter))))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(lessShortItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(lessShortItemDto.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(lessShortItemDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(lessShortItemDto.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(lessShortItemDto.getRequestId()), Long
                        .class));
    }

    @Test
    void shouldReturnAllRequestDtoWithPagination() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(requestService.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(request));

        mockMvc.perform(get("/requests/all")
                        .header(headerUserId, userId)
                        .param(paramFrom, from)
                        .param(paramSize, size))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(requestDto.getCreated().format(formatter))))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(lessShortItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(lessShortItemDto.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(lessShortItemDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(lessShortItemDto.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(lessShortItemDto.getRequestId()), Long
                        .class));
    }

    @Test
    void shouldReturnMethodParametersExceptionForGetAllRequestsWithPagination() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);

        String wrongFrom = "-1";
        mockMvc.perform(get("/requests/all")
                        .header(headerUserId, userId)
                        .param(paramFrom, wrongFrom)
                        .param(paramSize, size))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodParametersException))
                .andExpect(result -> assertEquals("Параметры запроса заданы не верно.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}