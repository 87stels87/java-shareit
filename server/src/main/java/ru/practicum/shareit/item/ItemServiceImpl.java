package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepo;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepo;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.helpers.HelperService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepo itemRepo;
    private final UserRepo userRepo;
    private final BookingRepo bookingRepo;
    private final CommentRepo commentRepo;
    private final HelperService helperService;
    private final ItemRequestRepo itemRequestRepo;

    @Transactional
    @Override
    public ItemDto getItemInfoById(long itemId, long userId) {
        helperService.checkItem(itemId);
        Item item = itemRepo.findById(itemId).get();

        ItemDto itemDto = ItemMapper.returnItemDto(item);

        helperService.checkUser(userId);

        if (item.getOwner().getId() == userId) {

            Optional<Booking> lastBooking = bookingRepo.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemId, Status.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepo.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemId, Status.APPROVED, LocalDateTime.now());

            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking.get()));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking.get()));
            } else {
                itemDto.setNextBooking(null);
            }
        }

        List<Comment> commentList = commentRepo.findByItemId(itemId);

        if (!commentList.isEmpty()) {
            itemDto.setComments(CommentMapper.returnCommentDtoList(commentList));
        } else {
            itemDto.setComments(Collections.emptyList());
        }

        return itemDto;
    }

    @Transactional
    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        helperService.checkUser(userId);
        User user = userRepo.findById(userId).get();
        Item item = ItemMapper.returnItem(itemDto, user);
        if (itemDto.getRequestId() != null) {
            helperService.checkRequest(itemDto.getRequestId());
            item.setRequest(itemRequestRepo.findById(itemDto.getRequestId()).get());
        }
        itemRepo.save(item);
        return ItemMapper.returnItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto changeItem(ItemDto itemDto, long itemId, long userId) {

        helperService.checkUser(userId);
        User user = userRepo.findById(userId).get();

        helperService.checkItem(itemId);
        Item item = ItemMapper.returnItem(itemDto, user);


        item.setId(itemId);

        if (!itemRepo.findByOwnerId(userId).contains(item)) {
            throw new NotFoundException(Item.class, "the ru.practicum.shareit.item was not found with the ru.practicum.shareit.user id " + userId);
        }

        Item newItem = itemRepo.findById(item.getId()).get();

        if (item.getName() != null) {
            newItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }

        itemRepo.save(newItem);

        return ItemMapper.returnItemDto(newItem);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getItemsByUserId(long userId, Integer from, Integer size) {
        helperService.checkUser(userId);
        PageRequest pageRequest = helperService.checkPageSize(from, size);
        List<ItemDto> resultList = new ArrayList<>();

        for (ItemDto itemDto : ItemMapper.returnItemDtoList(itemRepo.findByOwnerId(userId, pageRequest))) {

            Optional<Booking> lastBooking = bookingRepo.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemDto.getId(), Status.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepo.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemDto.getId(), Status.APPROVED, LocalDateTime.now());

            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking.get()));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking.get()));
            } else {
                itemDto.setNextBooking(null);
            }

            resultList.add(itemDto);
        }

        for (ItemDto itemDto : resultList) {

            List<Comment> commentList = commentRepo.findByItemId(itemDto.getId());

            if (!commentList.isEmpty()) {
                itemDto.setComments(CommentMapper.returnCommentDtoList(commentList));
            } else {
                itemDto.setComments(Collections.emptyList());
            }
        }

        return resultList;
    }

    @Transactional
    @Override
    public List<ItemDto> getItemsByKeyword(String text, Integer from, Integer size) {
        PageRequest pageRequest = helperService.checkPageSize(from, size);
        if (text.equals("")) {
            return Collections.emptyList();
        } else {
            return ItemMapper.returnItemDtoList(itemRepo.search(text, pageRequest));
        }
    }

    @Transactional
    @Override
    public CommentDto postComment(long userId, long itemId, CommentDto commentDto) {
        helperService.checkUser(userId);
        User user = userRepo.findById(userId).get();

        helperService.checkItem(itemId);
        Item item = itemRepo.findById(itemId).get();

        LocalDateTime dateTime = LocalDateTime.now();

        Optional<Booking> booking = bookingRepo.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, Status.APPROVED, dateTime);

        if (booking.isEmpty()) {
            throw new ValidationException("User " + userId + " not ru.practicum.shareit.booking this ru.practicum.shareit.item " + itemId);
        }

        Comment comment = CommentMapper.returnComment(commentDto, item, user, dateTime);
        commentRepo.save(comment);

        return CommentMapper.returnCommentDto(comment);
    }
}