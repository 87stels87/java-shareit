package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
public interface UserStorage {

    User create(User user);

    Collection<User> findAll();

    User update(User user, Long userId);

    void deleteById(Long userId);

    Optional<User> getById(Long id);
}