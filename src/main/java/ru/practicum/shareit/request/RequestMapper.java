package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;

public class RequestMapper {
    public static ItemRequestDto returnItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(Collections.emptyList())
                .build();
        return itemRequestDto;
    }

    public static ItemRequest returnItemRequest(ItemRequestDto itemRequestDto, User user) {
        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .requester(user)
                .build();
        return itemRequest;
    }
}