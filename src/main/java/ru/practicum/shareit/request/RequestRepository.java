package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Collection<Request> findAllByRequester_Id(Long userId);

    @Query(value = "SELECT r FROM Request r WHERE r.requester.id <> ?1")
    List<Request> findAllByRequester_Id(Long userId, Pageable pageable);

    Optional<Request> findItemByRequester_IdAndId(Long userId, Long requestId);
}
