package ru.practicum.shareit.item;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ItemRepositoryTest {

//    @Autowired
//    UserRepo userRepo;
//
//    @Autowired
//    ItemRepo itemRepo;
//
//    Item item1;
//
//    Item item2;
//
//    User user;
//
//    @BeforeEach
//    void beforeEach() {
//
//        user = userRepo.save(User.builder()
//                .id(1L)
//                .name("andrey")
//                .email("andrey@yandex.ru")
//                .build());
//
//        item1 = itemRepo.save(Item.builder()
//                .name("hammer")
//                .description("steel hammer")
//                .available(true)
//                .owner(user)
//                .build());
//
//        item2 = itemRepo.save(Item.builder()
//                .name("dvd")
//                .description("dvd panasonic")
//                .available(true)
//                .owner(user)
//                .build());
//    }
//
//    @Test
//    void searchInRepo() {
//
//        List<Item> items = itemRepo.search("panasonic", PageRequest.of(0, 1));
//
//        assertEquals(1, items.size());
//    }
//
//    @AfterEach
//    void afterEach() {
//        userRepo.deleteAll();
//        itemRepo.deleteAll();
//    }
}