package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exceptions.EmailNotExistException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UserRepo userRepo;

    User user1;

    User user2;

    UserDto userDto1;

    UserDto userDto2;


    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("andrey")
                .email("andrey@ya.ru")
                .build();

        userDto1 = UserMapper.returnUserDto(user1);

        user2 = User.builder()
                .id(2L)
                .name("ivan")
                .email("ivan@yandex.ru")
                .build();

        userDto2 = UserMapper.returnUserDto(user2);
    }



    @Test
    void createUser() {
        when(userRepo.save(any(User.class))).thenReturn(user1);

        UserDto userDtoTest = userService.create(userDto1);

        assertEquals(userDtoTest.getId(), userDto1.getId());
        assertEquals(userDtoTest.getName(), userDto1.getName());
        assertEquals(userDtoTest.getEmail(), userDto1.getEmail());

        verify(userRepo, times(1)).save(user1);
    }

    @Test
    void updateUser() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userRepo.findByEmail(anyString())).thenReturn(List.of(user1));
        when(userRepo.save(any(User.class))).thenReturn(user1);

        userDto1.setName("ivan");
        userDto1.setEmail("ivan@yandex.ru");

        UserDto userDtoUpdated = userService.updateUser(userDto1, 1L);

        assertEquals(userDtoUpdated.getName(), user1.getName());
        assertEquals(userDtoUpdated.getEmail(), user1.getEmail());


        verify(userRepo, times(1)).save(user1);
    }



    @Test
    void updateUserWithNotValidEmail() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userRepo.findByEmail(anyString())).thenReturn(List.of(user1));

        user1.setEmail("21.RU");
        assertThrows(EmailNotExistException.class, () -> userService.updateUser(userDto1, 2L));
    }

    @Test
    void updateUserWithEmptyEmail() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userRepo.findByEmail(anyString())).thenReturn(List.of(user1));

        user1.setEmail("");
        assertThrows(EmailNotExistException.class, () -> userService.updateUser(userDto1, 2L));
    }


    @Test
    void deleteUserById() {
        when(userRepo.existsById(anyLong())).thenReturn(true);

        userService.deleteUserById(1L);

        verify(userRepo, times(1)).deleteById(1L);
    }

    @Test
    void getUserById() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user1));

        UserDto userDtoTest = userService.getUserById(1L);

        assertEquals(userDtoTest.getId(), userDto1.getId());
        assertEquals(userDtoTest.getName(), userDto1.getName());
        assertEquals(userDtoTest.getEmail(), userDto1.getEmail());

        verify(userRepo, times(1)).findById(1L);
    }

    @Test
    void findAllUsers() {
        when(userRepo.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> userDtoList = userService.findAll();
        assertEquals(userDtoList, List.of(userDto1, userDto2));

        verify(userRepo, times(1)).findAll();
    }

    @Test
    void findAllUsers2time() {
        when(userRepo.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> userDtoList1 = userService.findAll();
        List<UserDto> userDtoList2 = userService.findAll();
        assertEquals(userDtoList1, List.of(userDto1, userDto2));

        verify(userRepo, times(2)).findAll();
    }

}
