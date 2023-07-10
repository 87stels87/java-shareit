package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto getItemInfo(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.getItemInfo(itemId));
    }

    public ItemDto addNewItem(ItemDto itemDto, Long userId) {
        Optional<User> user = userStorage.getById(userId);
        if (user.isEmpty()) throw new NotFoundException("Юзер не найден");
        if (itemDto.getName() == null || itemDto.getDescription() == null || itemDto.getAvailable() == null)
            throw new BadRequestException("У вещи отсутствуют/пусты обязательные реквизиты");
        if (itemDto.getName().isBlank() || itemDto.getDescription().isBlank())
            throw new BadRequestException("У вещи отсутствуют/пусты обязательные реквизиты");
        return ItemMapper.toItemDto(itemStorage.add(ItemMapper.toItem(itemDto, user.get(), null)));
    }

    public ItemDto changeItem(Long itemId, Long userId, ItemDto itemDto) {
        if (userStorage.getById(userId).isEmpty()) throw new NotFoundException("Юзер не найден");
        Item item = ItemMapper.toItem(itemDto, userStorage.getById(userId).get(), null);
        return ItemMapper.toItemDto(itemStorage.change(itemId, userId, item));
    }

    public Collection<ItemDto> getItemsByUserId(Long userId) {
        return itemStorage.getById(userId).stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> getSearchByWord(String text) {
        return itemStorage.getByKeyWords(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}