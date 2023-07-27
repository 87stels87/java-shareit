package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.Collection;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;


    @PostMapping
    public ItemDto addNewItem(@RequestBody @Valid ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("addNewItem = {} by userId = {}", itemDto, userId);
        return itemService.addNewItem(userId, itemDto);
    }


    @PatchMapping("/{itemId}")
    public ItemDto changeItem(@PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("changeItem, item = {}", itemDto);
        return itemService.changeItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemInfo(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("getItemInfo by id, id = {}", itemId);
        return itemService.getItemInfoById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("getAllItems by userid ={}", userId);
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getSearchItemsByWord(@RequestParam String text) {
        log.info("search by text: ", text);
        return itemService.getItemsByKeyword(text);
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                  @RequestBody @Valid CommentDto commentDto
    ) {
        log.info("postComment, id = {}, comment = {}, userId = {}", itemId, commentDto.getText());
        return itemService.postComment(userId, itemId, commentDto);
    }
}