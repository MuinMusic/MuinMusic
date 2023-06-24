package com.mucompany.muinmusic.facade;

import com.mucompany.muinmusic.Item.domain.Item;
import com.mucompany.muinmusic.Item.repository.ItemRepository;
import com.mucompany.muinmusic.exception.ItemNotFoundException;
import com.mucompany.muinmusic.exception.OrderNotFoundException;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
import com.mucompany.muinmusic.order.app.OrderRequest;
import com.mucompany.muinmusic.order.domain.OrderItem;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class RedissonLockFacadeTest {

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
    private RedissonLockFacade redissonLockFacade;

    private void h2DBResetAutoIncrement() {
        jdbcTemplate.execute("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE orders ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE item ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE order_item ALTER COLUMN id RESTART WITH 1");
    }

    @BeforeEach
    void setup() {
        h2DBResetAutoIncrement();

        Member member = new Member("dp", "seoul");
        Item item = new Item("jpaBook", 20000, 20);

        memberRepository.save(member);
        itemRepository.save(item);

        OrderItem orderItem = new OrderItem(item.getId(), 1, 60000);
        orderItemRepository.save(orderItem);
    }

    @AfterEach
    void deleteAll() {
        orderRepository.deleteAll();
        orderItemRepository.deleteAll();
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
                OrderItem orderItem = new OrderItem(1L, 1, 60000);
                orderItemRepository.save(orderItem);

                List<Long> orderItemIdList = new ArrayList<>();
                orderItemIdList.add(orderItem.getId());

                OrderRequest orderRequest = OrderRequest.builder()
                        .memberId(1L)
                        .orderItemIdList(orderItemIdList)
                        .address("seoul")
                        .orderDate(LocalDateTime.now())
                        .build();
                try {
                    redissonLockFacade.placeOrder(orderRequest);

                } catch (Exception e) {
                    System.out.println(e);
                }

                latch.countDown();
            });
        }

        latch.await();

        Item findItem = itemRepository.findById(1L).orElseThrow(() -> new ItemNotFoundException());

        assertEquals(0, findItem.getStock());
    }

    @DisplayName(value = "초과 주문하면 실패")
    @Test
    public void t2() throws InterruptedException {
        int threadCount = 101;

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                OrderItem orderItem = new OrderItem(1L, 1, 60000);
                orderItemRepository.save(orderItem);

                List<Long> orderItemIdList = new ArrayList<>();
                orderItemIdList.add(orderItem.getId());

                OrderRequest orderRequest = OrderRequest.builder()
                        .memberId(1L)
                        .orderItemIdList(orderItemIdList)
                        .address("seoul")
                        .orderDate(LocalDateTime.now())
                        .build();
                try {

                    redissonLockFacade.placeOrder(orderRequest);

                } catch (Exception e) {
                    log.error("Exception occurred: {}", e.getMessage());
                }

                latch.countDown();
            });
        }

        latch.await();

        assertThrows(OrderNotFoundException.class, () -> {
            orderRepository.findById(101L).orElseThrow(() -> new OrderNotFoundException());
        });
    }
}

