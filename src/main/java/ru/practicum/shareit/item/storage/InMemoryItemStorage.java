package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

//    private Long getNextId() {
//        return ++id;
//    }

    @Override
    public Item getItemInfo(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item add(Item item) {
      //  Long itemId = getNextId();
        Long itemId = ++id;
        item.setId(itemId);
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item change(Long itemId, Long userId, Item itemUpdate) {
        if (!items.containsKey(itemId)) throw new BadRequestException("Такого id нет");
        Item item = items.get(itemId);
        if (!Objects.equals(item.getOwner().getId(), userId))
            throw new NotFoundException("Редактирование вещи не ее владельцем");
        if (itemUpdate.getName() != null) item.setName(itemUpdate.getName());
        if (itemUpdate.getDescription() != null) item.setDescription(itemUpdate.getDescription());
        if (itemUpdate.getAvailable() != null) item.setAvailable(itemUpdate.getAvailable());
        items.put(itemId, item);
        return item;
    }

    @Override
    public Collection<Item> getById(Long userId) {
        return items.values();
    }

    @Override
    public Collection<Item> getByKeyWords(String text) {
        if (text.isBlank()) return List.of();
        return items.values().stream()
                .filter(item ->
                        item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase())
                )
                .filter(item -> item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }
}