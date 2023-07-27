package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItemInfoById(long itemId, long userId);

    ItemDto addNewItem(long userId, ItemDto itemDto);

    ItemDto changeItem(long itemId, long userId, ItemDto itemDto);

    List<ItemDto> getItemsByUserId(long userId);

    List<ItemDto> getItemsByKeyword(String text);

    CommentDto postComment(long userId, long itemId, CommentDto commentDto);

}