package ru.practicum.shareit.server.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.dto.BookingMapper;
import ru.practicum.shareit.server.booking.model.BookingStatus;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.repository.CommentRepository;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.ItemNotFoundException;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.server.item.dto.ItemDto;

import java.time.LocalDateTime;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service("ItemServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public Item create(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Item update(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Item getItemByIdOrElseThrow(long itemId) throws ItemNotFoundException {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.debug("Вещь с itemId  = {} не найдена.", itemId);
            throw new ItemNotFoundException(String.format("Вещь с itemId = %s не найдена.",
                    itemId));
        });
    }

    @Override
    public Item getItemByUserIdAndItemId(long userId, long itemId) throws ItemNotFoundException {
        return itemRepository.findItemByOwner_IdAndId(userId, itemId).orElseThrow(() -> {
            log.debug("Вещь с itemId  = {} у пользователя с userId = {} не найдена.", itemId, userId);
            throw new ItemNotFoundException(String.format("Вещь с itemId = %s у пользователя с userId = %s не найдена.",
                    itemId, userId));
        });
    }

    @Override
    public ItemDto getItemDtoByOwnerIdAndItemId(long userId, long itemId) throws ItemNotFoundException {
        ItemDto itemDto = ItemMapper.toItemDto(getItemByIdOrElseThrow(itemId));

        if (itemRepository.findItemByOwner_IdAndId(userId, itemId).isPresent()) {
            setBookingsToItemDto(itemDto);
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItemsDtoByOwnerId(long userId, int from, int size) {
        List<Item> items = itemRepository.findAllByOwner_Id(userId, PageRequest.of(from, size));

        return items.stream()
                .map(ItemMapper::toItemDto)
                .peek(this::setBookingsToItemDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findItemsBySearch(String text, int from, int size) {
        String lowercaseText = text.toLowerCase();

        return itemRepository.findAllBySearch(lowercaseText, PageRequest.of(from, size));
    }

    @Override
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    private void setBookingsToItemDto(ItemDto itemDto) {
        if (bookingRepository.findFirstByItem_IdAndStatusAndStartDateIsBeforeOrderByEndDateDesc(itemDto.getId(),
                BookingStatus.APPROVED, LocalDateTime.now()).isPresent()) {

            itemDto.setLastBooking(BookingMapper.toShortBookingDto(bookingRepository
                    .findFirstByItem_IdAndStatusAndStartDateIsBeforeOrderByEndDateDesc(itemDto.getId(),
                            BookingStatus.APPROVED, LocalDateTime.now()).get()));

        }
        if (bookingRepository.findFirstByItem_IdAndStatusAndStartDateIsAfterOrderByStartDateAsc(itemDto.getId(),
                BookingStatus.APPROVED, LocalDateTime.now()).isPresent()) {

            itemDto.setNextBooking(BookingMapper.toShortBookingDto(bookingRepository
                    .findFirstByItem_IdAndStatusAndStartDateIsAfterOrderByStartDateAsc(itemDto.getId(),
                            BookingStatus.APPROVED, LocalDateTime.now()).get()));

        }
    }
}