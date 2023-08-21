package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;


    @PostMapping
    public ResponseEntity<ItemDto> addNewItem(@RequestBody @Valid ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("addNewItem = {} by userId = {}", itemDto, userId);
        return ResponseEntity.ok(itemService.addNewItem(userId, itemDto));
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> changeItem(@PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody ItemDto itemDto) {
        log.info("changeItem, ru.practicum.shareit.item = {}", itemDto);
        return ResponseEntity.ok(itemService.changeItem(itemDto, itemId, userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemInfo(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("getItemInfo by id, id = {}", itemId);
        return ResponseEntity.ok(itemService.getItemInfoById(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(required = false, defaultValue = "0") Integer from,
                                                     @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("getAllItems by userid ={}", userId);
        return ResponseEntity.ok(itemService.getItemsByUserId(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> getSearchItemsByWord(@RequestParam String text,
                                                              @RequestParam(required = false, defaultValue = "0") Integer from,
                                                              @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("search by text: ", text);
        return ResponseEntity.ok(itemService.getItemsByKeyword(text, from, size));
    }


    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> postComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                                  @RequestBody @Valid CommentDto commentDto
    ) {
        log.info("postComment, id = {}, comment = {}, userId = {}", itemId, commentDto.getText());
        return ResponseEntity.ok(itemService.postComment(userId, itemId, commentDto));
    }
}