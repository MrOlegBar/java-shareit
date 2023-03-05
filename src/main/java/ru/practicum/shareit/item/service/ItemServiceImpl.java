package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ShortItemDto;

import java.time.LocalDateTime;
import java.util.Collection;

import java.util.stream.Collectors;

@Service("ItemServiceImpl")
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
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
    public ShortItemDto getItemDtoByItemId(long userId, long itemId) throws ItemNotFoundException {
        ShortItemDto shortItemDto = ItemMapper.toShortItemDto(getItemById(itemId));

        if (itemRepository.findItemByOwner_IdAndId(userId, itemId).isPresent()) {
            setBookingsToShortItemDto(shortItemDto);
        }
        return shortItemDto;
    }

    @Override
    public Collection<ShortItemDto> getAllItemsDtoByUserId(long userId) {
        Collection<Item> items = itemRepository.findAllByOwner_Id(userId);

        return items.stream()
                .map(ItemMapper::toShortItemDto)
                .peek(this::setBookingsToShortItemDto)
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

    private void setBookingsToShortItemDto(ShortItemDto shortItemDto) {
        LocalDateTime dateTimeNow = LocalDateTime.now();

        if (bookingRepository.findFirstByItem_IdAndEndDateIsBeforeOrderByEndDateAsc(shortItemDto.getId(), dateTimeNow).isPresent()) {

            shortItemDto.setLastBooking(BookingMapper.toDtoForItem(bookingRepository
                    .findFirstByItem_IdAndEndDateIsBeforeOrderByEndDateAsc(shortItemDto.getId(), dateTimeNow).get()));

            setNextBooking(shortItemDto, dateTimeNow);
        } else if (bookingRepository.findFirstByItem_IdAndStartDateIsAfterOrderByStartDateAsc(shortItemDto.getId(), dateTimeNow).isPresent()) {
            shortItemDto.setNextBooking(BookingMapper.toDtoForItem(bookingRepository
                    .findFirstByItem_IdAndStartDateIsAfterOrderByStartDateAsc(shortItemDto.getId(), dateTimeNow).get()));

            setNextBooking(shortItemDto, dateTimeNow);
        }
    }

    private void setNextBooking(ShortItemDto shortItemDto, LocalDateTime dateTimeNow) {
        if (bookingRepository.findFirstByItem_IdAndStartDateIsAfterOrderByStartDateAsc(shortItemDto.getId(), shortItemDto.getLastBooking().getEnd())
                .isPresent()) {

            shortItemDto.setNextBooking(BookingMapper.toDtoForItem(bookingRepository
                    .findFirstByItem_IdAndStartDateIsAfterOrderByStartDateAsc(shortItemDto.getId(), shortItemDto.getLastBooking().getEnd()).get()));

        } else if (bookingRepository.findFirstByItem_IdAndStartDateIsAfterOrderByStartDateAsc(shortItemDto.getId(), dateTimeNow).isPresent()) {

            shortItemDto.setNextBooking(BookingMapper.toDtoForItem(bookingRepository
                    .findFirstByItem_IdAndStartDateIsAfterOrderByStartDateAsc(shortItemDto.getId(), dateTimeNow).get()));
        }
    }
}