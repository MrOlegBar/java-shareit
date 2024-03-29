package ru.practicum.shareit.server.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.request.Request;
import ru.practicum.shareit.server.request.RequestNotFoundException;
import ru.practicum.shareit.server.request.RequestRepository;

import java.util.Collection;
import java.util.List;

@Service("RequestServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;

    @Override
    public Request create(Request request) {
        return requestRepository.save(request);
    }

    @Override
    public Request getRequestByIdOrElseThrow(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> {
            log.debug("Запрос с requestId  = {} не найден.", requestId);
            throw new RequestNotFoundException(String.format("Запрос с requestId = %s не найден.",
                    requestId));
        });
    }

    @Override
    public Collection<Request> getAllRequestsByUserId(long userId) {
        return requestRepository.findAllByRequester_Id(userId);
    }

    @Override
    public List<Request> getAllItemRequests(long userId, int from, int size) {
        return requestRepository.findAllByRequester_Id(userId, PageRequest.of(from, size,
                Sort.Direction.DESC, "created"));
    }
}