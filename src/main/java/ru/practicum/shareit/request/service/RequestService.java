package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.Request;

import java.util.Collection;
import java.util.List;

public interface RequestService {
    Request create(Request request);

    Collection<Request> getAllRequestsByUserId(long userId);

    List<Request> getAllItemRequests(long userId, int from, int size);

    Request getRequestById(long requestId);
}