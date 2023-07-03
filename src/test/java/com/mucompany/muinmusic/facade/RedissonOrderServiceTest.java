package com.mucompany.muinmusic.facade;

import com.mucompany.muinmusic.cart.domain.Cart;
import com.mucompany.muinmusic.cart.domain.CartItem;
import com.mucompany.muinmusic.cart.domain.repository.CartItemRepository;
import com.mucompany.muinmusic.cart.domain.repository.CartRepository;
import com.mucompany.muinmusic.exception.ItemNotFoundException;
import com.mucompany.muinmusic.item.domain.Item;
import com.mucompany.muinmusic.item.repository.ItemRepository;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
import com.mucompany.muinmusic.order.app.OrderRequest;
import com.mucompany.muinmusic.order.domain.repository.OrderItemRepository;
import com.mucompany.muinmusic.order.domain.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class RedissonOrderServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedissonOrderService redissonOrderService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    private void h2ResetAutoIncrement() {
        jdbcTemplate.execute("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE orders ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE item ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE order_item ALTER COLUMN id RESTART WITH 1");
    }

    @BeforeEach
    void setup() {
        h2ResetAutoIncrement();

//        Member member = new Member("dp", "seoul");
//        memberRepository.save(member);

        Item item = new Item("jpaBook1", 20000, 100);
        Item item2 = new Item("jpaBook2", 20000, 100);
        Item item3 = new Item("jpaBook3", 20000, 100);
        itemRepository.save(item);
        itemRepository.save(item2);
        itemRepository.save(item3);

//        CartItem cartItem = new CartItem(item.getId(), 1, 60000);
//        CartItem cartItem2 = new CartItem(item2.getId(), 1, 60000);
//        CartItem cartItem3 = new CartItem(item3.getId(), 1, 60000);
//
//        cartItemRepository.save(cartItem);
//        cartItemRepository.save(cartItem2);
//        cartItemRepository.save(cartItem3);

//        List<CartItem> cartItems = new ArrayList<>();
//        cartItems.add(cartItem);
//        cartItems.add(cartItem2);
//        cartItems.add(cartItem3);

//        Cart cart = new Cart(member,cartItems);
//        cartRepository.save(cart);
    }

    @AfterEach
    void deleteAll() {
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        cartItemRepository.deleteAll();
        itemRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @DisplayName(value = "placeOrder() -> orderService.place() redisson 동시성 적용시키기 ")
    @Test
    public void t1() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {

                Member member = new Member("dp", "seoul");
                memberRepository.save(member);

                CartItem cartItem = new CartItem(1L, 1, 60000);
                CartItem cartItem2 = new CartItem(2L, 1, 60000);
                CartItem cartItem3 = new CartItem(3L, 1, 60000);
                cartItemRepository.save(cartItem);
                cartItemRepository.save(cartItem2);
                cartItemRepository.save(cartItem3);

                CartItem findCartItem1 = cartItemRepository.findById(cartItem.getId()).orElseThrow();
                CartItem findCartItem2 = cartItemRepository.findById(cartItem2.getId()).orElseThrow();
                CartItem findCartItem3 = cartItemRepository.findById(cartItem3.getId()).orElseThrow();

                List<CartItem> cartItemList = List.of(cartItem, cartItem2, cartItem3);

                Member findMember = memberRepository.findById(member.getId()).orElseThrow();
                Cart cart = new Cart(findMember, cartItemList);
                cartRepository.save(cart);
                System.out.println("1111"+cart.getCartItems());

                OrderRequest orderRequest = OrderRequest.builder()
                        .memberId(member.getId())
                        .cartId(cart.getId())
                        .address("seoul")
                        .orderDate(LocalDateTime.now())
                        .build();
                try {
                    redissonOrderService.placeOrder(orderRequest);
                } catch (Exception e) {
                    log.info("exception = " + e);
                }
                latch.countDown();
            });
        }
        latch.await();
        Item findItem = itemRepository.findById(1L).orElseThrow(() -> new ItemNotFoundException());
        Item findItem2 = itemRepository.findById(2L).orElseThrow(() -> new ItemNotFoundException());
        Item findItem3 = itemRepository.findById(3L).orElseThrow(() -> new ItemNotFoundException());
        assertEquals(0, findItem.getStock());
        assertEquals(0, findItem2.getStock());
        assertEquals(0, findItem3.getStock());
    }

//    @DisplayName(value = "초과 주문하면 실패")
//    @Test
//    public void t2() throws InterruptedException {
//        int threadCount = 101;
//
//        ExecutorService executorService = Executors.newFixedThreadPool(32);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        for (int i = 0; i < threadCount; i++) {
//            executorService.submit(() -> {
//                CartItem cartItem = new CartItem(1L, 1, 60000);
//                cartItemRepository.save(cartItem);
//
//                List<Long> cartItemIdList = new ArrayList<>();
//                cartItemIdList.add(cartItem.getId());
//
//                OrderRequest orderRequest = OrderRequest.builder()
//                        .memberId(1L)
//                        .cartItemIdList(cartItemIdList)
//                        .address("seoul")
//                        .orderDate(LocalDateTime.now())
//                        .build();
//                try {
//
//                    redissonOrderService.placeOrder(orderRequest);
//
//                } catch (Exception e) {
//                    log.error("Exception occurred: {}", e.getMessage());
//                }
//
//                latch.countDown();
//            });
//        }
//
//        latch.await();
//
//        assertThrows(OrderNotFoundException.class, () -> {
//            orderRepository.findById(101L).orElseThrow(() -> new OrderNotFoundException());
//        });
//    }
}

