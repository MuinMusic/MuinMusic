package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.Item.domain.Item;
import com.mucompany.muinmusic.Item.repository.ItemRepository;
import com.mucompany.muinmusic.exception.ItemNotFoundException;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
import com.mucompany.muinmusic.order.domain.Order;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.domain.OrderStatus;
import com.mucompany.muinmusic.order.domain.repository.OrderItemRepository;
import com.mucompany.muinmusic.order.domain.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        Item item = new Item("jpaBook", 20000, 100);

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

    @Transactional
    @DisplayName(value = "orderId, memberId 값 유효하면 취소 성공 ")
    @Test
    void t2() {
        orderSave();

        Order order = orderRepository.findById(1L).orElseThrow();

        orderService.cancel(order.getId(), order.getMember().getId());

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Disabled
    @DisplayName(value = "placeOrder() -> pessimisticLock 적용, 동시성 성공")
    @Test
    public void t3() throws InterruptedException {
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
                    orderService.placeOrder(orderRequest);

                } catch (Exception e) {
                    log.error(e.getMessage());
                }

                latch.countDown();
            });
        }

        latch.await();

        Item findItem = itemRepository.findById(1L).orElseThrow(() -> new ItemNotFoundException());

        assertEquals(0, findItem.getStock());
    }

    private void orderSave() {

        Member member = memberRepository.findById(1L).orElseThrow();

        OrderItem orderItem = orderItemRepository.findById(1L).orElseThrow();
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);

        Order order = Order.builder()
                .member(member)
                .orderItems(orderItemList)
                .address("seoul")
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.ORDERED)
                .build();

        orderRepository.save(order);
    }
}
