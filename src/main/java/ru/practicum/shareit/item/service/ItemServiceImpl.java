package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Collection;

import java.util.stream.Collectors;

@Service("ItemServiceImpl")
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, BookingRepository bookingRepository,
                           CommentRepository commentRepository) {

        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Item create(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Item getItemById(long itemId) throws ItemNotFoundException {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.debug("Вещь с itemId  = {} не найдена.", itemId);
            throw new ItemNotFoundException(String.format("Вещь с itemId = %s не найдена.",
                    itemId));
        });
    }

    @Override
    public ItemDto getItemDtoByItemId(long userId, long itemId) throws ItemNotFoundException {
        ItemDto itemDto = ItemMapper.toItemDto(getItemById(itemId));

        if (itemRepository.findItemByOwner_IdAndId(userId, itemId).isPresent()) {
            setBookingsToItemDto(itemDto);
        }
        return itemDto;
    }

    @Override
    public Collection<ItemDto> getAllItemsDtoByUserId(long userId) {
        Collection<Item> items = itemRepository.findAllByOwner_Id(userId);

        return items.stream()
                .map(ItemMapper::toItemDto)
                .peek(this::setBookingsToItemDto)
                .collect(Collectors.toList());
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
    public Item update(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Collection<Item> findItemsBySearch(String text) {
        String lowercaseText = text.toLowerCase();
        return itemRepository.findAll().stream()
                .filter(savedItem -> (savedItem.getAvailable())
                        && (savedItem.getName().toLowerCase().contains(lowercaseText)
                        || savedItem.getDescription().toLowerCase().contains(lowercaseText)))
                .collect(Collectors.toList());
    }

    @Override
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    private void setBookingsToItemDto(ItemDto itemDto) {
        LocalDateTime dateTimeNow = LocalDateTime.now();

        if (bookingRepository.findFirstByItem_IdAndStatusAndEndDateIsBeforeOrderByEndDateAsc(itemDto.getId(),
                BookingStatus.APPROVED, dateTimeNow).isPresent()) {

            itemDto.setLastBooking(BookingMapper.toShortBookingDto(bookingRepository
                    .findFirstByItem_IdAndStatusAndEndDateIsBeforeOrderByEndDateAsc(itemDto.getId(),
                            BookingStatus.APPROVED, dateTimeNow).get()));

        }
        if (bookingRepository.findFirstByItem_IdAndStatusAndStartDateIsAfterOrderByStartDateAsc(itemDto.getId(),
                BookingStatus.APPROVED, dateTimeNow).isPresent()) {

            itemDto.setNextBooking(BookingMapper.toShortBookingDto(bookingRepository
                    .findFirstByItem_IdAndStatusAndStartDateIsAfterOrderByStartDateAsc(itemDto.getId(),
                            BookingStatus.APPROVED, dateTimeNow).get()));

        }
    }
}