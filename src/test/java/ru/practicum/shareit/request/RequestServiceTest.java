package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
class RequestServiceTest {
    @Autowired
    private RequestService requestService;
    @MockBean
    private RequestRepository requestRepository;
    long wrongRequestId = 99L;
    long requestId = 1L;
    long userId = 1L;
    int from = 0;
    int size = 10;
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

    @Test
    public void shouldReturnCreatedRequest() {
        Mockito.when(requestRepository.save(any(Request.class)))
                .thenReturn(request);

        Request actual = requestService.create(request);

        assertEquals(request, actual);
    }

    @Test
    public void shouldReturnRequestById() {
        Mockito.when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));

        Request actual = requestService.getRequestById(requestId);

        assertEquals(request, actual);
    }

    @Test
    public void shouldReturnRequestNotFoundException() {
        Mockito.when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(RequestNotFoundException.class, () ->
                requestService.getRequestById(wrongRequestId));

        String expected = String.format("Запрос с requestId = %s не найден.", wrongRequestId);
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnAllRequestByUserId() {
        Mockito.when(requestRepository.findAllByRequester_Id(anyLong()))
                .thenReturn(List.of(request));

        Collection<Request> foundRequests = requestService.getAllRequestsByUserId(userId);

        assertEquals(List.of(request), foundRequests);
    }

    @Test
    public void shouldReturnAllRequestByUserIdWithPagination() {
        Mockito.when(requestRepository.findAllByRequester_Id(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(request));

        Collection<Request> actual = requestService.getAllItemRequests(userId, from, size);

        assertEquals(List.of(request), actual);
    }
}
