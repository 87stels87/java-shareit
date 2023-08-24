package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItemInfoById(long itemId, long userId);

    ItemDto addNewItem(long userId, ItemDto itemDto);

    ItemDto changeItem(ItemDto itemDto, long userId, long itemId);

    List<ItemDto> getItemsByUserId(long userId, Integer from, Integer size);

    List<ItemDto> getItemsByKeyword(String text, Integer from, Integer size);

    CommentDto postComment(long userId, long itemId, CommentDto commentDto);

}