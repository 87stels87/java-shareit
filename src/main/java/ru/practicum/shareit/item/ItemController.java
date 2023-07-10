package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.Map;

@Validated
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addNewItem(@RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на добавление новой вещи {} юзером с id = {}", itemDto, userId);
        return itemService.addNewItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto changeItem(@PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("Запрос на редактирование атрибута вещи {}", itemDto);
        return itemService.changeItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemInfo(@PathVariable Long itemId) {
        log.info("Запрос на получение информации о вещи с id = {}", itemId);
        return itemService.getItemInfo(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение списка вещей юзера с id ={}", userId);
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getSearchItemsByWord(@RequestParam String text) {
        log.info("запрос на поиск по тексту", text);
        return itemService.getSearchByWord(text);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(final NotFoundException e) {
        return Map.of(
                "Сообщение об ошибке", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final BadRequestException e) {
        return Map.of(
                "Сообщение об ошибке", e.getMessage()
        );
    }
}