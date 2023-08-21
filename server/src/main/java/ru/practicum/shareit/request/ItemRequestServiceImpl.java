package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.helpers.HelperService;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepo;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRepo itemRepo;
    private final UserRepo userRepo;
    private final ItemRequestRepo itemRequestRepository;
    private final HelperService helperService;

    @Transactional
    @Override
    public ItemRequestDto addRequest(ItemRequestDto itemRequestDto, long userId) {

        helperService.checkUser(userId);

        User user = userRepo.findById(userId).get();

        ItemRequest itemRequest = RequestMapper.returnItemRequest(itemRequestDto, user);

        itemRequest = itemRequestRepository.save(itemRequest);

        return RequestMapper.returnItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getRequests(long userId) {

        helperService.checkUser(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterIdOrderByCreatedAsc(userId);

        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            result.add(addItemsToRequest(itemRequest));
        }
        return result;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {

        PageRequest pageRequest = helperService.checkPageSize(from, size);

        Page<ItemRequest> itemRequests = itemRequestRepository.findByIdIsNotOrderByCreatedAsc(userId, pageRequest);

        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            result.add(addItemsToRequest(itemRequest));
        }
        return result;
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {

        helperService.checkUser(userId);
        helperService.checkRequest(requestId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId).get();

        return addItemsToRequest(itemRequest);
    }

    @Override
    public ItemRequestDto addItemsToRequest(ItemRequest itemRequest) {

        ItemRequestDto itemRequestDto = RequestMapper.returnItemRequestDto(itemRequest);
        List<Item> items = itemRepo.findByRequestId(itemRequest.getId());
        itemRequestDto.setItems(ItemMapper.returnItemDtoList(items));

        return itemRequestDto;
    }
}