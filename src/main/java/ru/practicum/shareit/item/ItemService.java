package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItemInfoById(Long itemId, Long userId);

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto changeItem(Long itemId, Long userId, ItemDto itemDto);

    List<ItemDto> getItemsByUserId(Long userId);

    List<ItemDto> getItemsByKeyword(String text);

    CommentDto postComment(long userId, long itemId, CommentDto commentDto);

}