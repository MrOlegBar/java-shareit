package ru.practicum.shareit.server.request.service;

import ru.practicum.shareit.server.request.Request;

import java.util.Collection;
import java.util.List;

public interface RequestService {
    Request create(Request request);

    Collection<Request> getAllRequestsByUserId(long userId);

    List<Request> getAllItemRequests(long userId, int from, int size);

    Request getRequestByIdOrElseThrow(long requestId);
}