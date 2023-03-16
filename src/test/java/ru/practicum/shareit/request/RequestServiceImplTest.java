package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
public class RequestServiceImplTest {
    @Autowired
    private RequestServiceImpl requestService;
    @MockBean
    private RequestRepository requestRepository;
    private final User user = new User(1L, "user@user.com", "user");
    private final Item item = new Item(1L, "Дрель", "Простая дрель", true, user,
            new Request(), new HashSet<>());
    private final Set<Item> items = new HashSet<>();
    private Request request;
    private Request testRequest;
    private List<Request> testRequests;

    @BeforeEach
    public void setUp() {
        items.add(item);
        request = new Request("Хотел бы воспользоваться щёткой для обуви", user, items);
        testRequest = new Request(1L, "Хотел бы воспользоваться щёткой для обуви", user, items);

        testRequests = new ArrayList<>();
        testRequests.add(testRequest);
    }

    @Test
    public void shouldReturnCreatedRequest() {
        Mockito.when(requestRepository.save(any(Request.class)))
                .thenReturn(testRequest);

        Request foundRequest = requestService.create(request);

        assertNotNull(foundRequest);
        assertEquals(testRequest, foundRequest);
    }

    @Test
    public void shouldReturnRequestById() {
        Mockito.when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testRequest));

        Request foundRequest = requestService.getRequestById(1L);

        assertNotNull(foundRequest);
        assertEquals(testRequest, foundRequest);
    }

    @Test
    public void shouldReturnRequestNotFoundException() {
        Mockito.when(requestRepository.findById(99L))
                .thenThrow(new RequestNotFoundException(String.format("Запрос с requestId  = %s не найден.",
                        99L)));

        Exception exception = assertThrows(RequestNotFoundException.class, () ->
                requestService.getRequestById(99L));

        String expectedMessage = "Запрос с requestId  = 99 не найден.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void shouldReturnAllRequestByUserId() {
        Mockito.when(requestRepository.findAllByRequester_Id(anyLong()))
                .thenReturn(testRequests);

        Collection<Request> foundRequests = requestService.getAllRequestsByUserId(1L);

        assertNotNull(foundRequests);
        assertEquals(testRequests, foundRequests);
    }

    @Test
    public void shouldReturnAllRequestByUserIdWithPagination() {
        Mockito.when(requestRepository.findAllByRequester_Id(anyLong(), any(PageRequest.class)))
                .thenReturn(testRequests);

        Collection<Request> foundRequests = requestService.getAllItemRequests(1L, 0, 10);

        assertNotNull(foundRequests);
        assertEquals(testRequests, foundRequests);
    }
}
