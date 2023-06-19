package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.Item.domain.Item;
import com.mucompany.muinmusic.Item.repository.ItemRepository;
import com.mucompany.muinmusic.exception.ItemNotFoundException;
import com.mucompany.muinmusic.exception.MemberNotFoundException;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
import com.mucompany.muinmusic.order.domain.Order;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.domain.OrderStatus;
import com.mucompany.muinmusic.order.domain.repository.OrderItemRepository;
import com.mucompany.muinmusic.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
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
    @Autowired
    private StockService stockService;


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
        Item item2 = new Item("springBook", 20000, 100);
        OrderItem orderItem = new OrderItem(item, 1, 60000);
        OrderItem orderItem2 = new OrderItem(item2, 1, 20000);

        memberRepository.save(member);
        itemRepository.save(item);
        itemRepository.save(item2);
        orderItemRepository.save(orderItem);
//        orderItemRepository.save(orderItem2);
    }

    @DisplayName(value = "OrderRequest 값 유효하면 주문 저장 성공 및 OrderResponse 반환 성공")
    @Test
    void t1() {
        //이메일로 멤버 찾기 unique
        OrderResponse orderResponse = orderSave();

        Order order = orderRepository.findById(orderResponse.getMemberId()).orElseThrow(MemberNotFoundException::new);
        Item item = itemRepository.findById(1L).orElseThrow();

        assertThat(order.getOrderStatus()).isEqualTo(orderResponse.getOrderStatus());
        assertThat(order.getMember().getAddress()).isEqualTo(orderResponse.getAddress());
        assertThat(order.getOrderItems().size()).isEqualTo(orderResponse.getOrderItemIdList().size());
        assertThat(item.getStock()).isEqualTo(99);
    }

    @DisplayName(value = "orderId, memberId 값 유효하면 취소 성공 ")
    @Test
    void t2() {
        OrderResponse orderResponse = orderSave();

        Long orderItemId = orderResponse.getOrderItemIdList().get(0);

        Order order = orderRepository.findByOrderItemsId(orderItemId);
        Long memberId = orderResponse.getMemberId();

        orderService.cancel(order.getId(), memberId);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Disabled
    @DisplayName(value = "pessimisticLock 적용, 동시성 성공")
    @Test
    public void 동시에_100명이_주문5() throws InterruptedException {
        int threadCount = 100;
        Item item = new Item("ticket", 20000, 100);
        itemRepository.save(item);

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                OrderItem orderItem = new OrderItem(item, 1, 60000);
                orderItemRepository.save(orderItem);

                List<Long> orderItemIdList = new ArrayList<>();
                orderItemIdList.add(orderItem.getId());

                stockService.decrease(item.getId(), orderItem);
                latch.countDown();
            });
        }

        latch.await();

        Item item2 = itemRepository.findById(item.getId()).orElseThrow();

        assertEquals(0, item2.getStock());
    }

    @Disabled
    @DisplayName(value = "pessimisticLock 적용 안할경우 동시성 실패 테스트")
    @Test
    public void 동시에_100명이_주문6() throws InterruptedException {
        int threadCount = 100;
        List<OrderItem> orderItemList = new ArrayList<>();
        Item item = new Item("jpaBook", 20000, 100);
        itemRepository.save(item);

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                OrderItem orderItem = new OrderItem(item, 1, 60000);
                orderItemRepository.save(orderItem);

                List<Long> orderItemIdList = new ArrayList<>();
                orderItemIdList.add(orderItem.getId());

                stockService.decrease2(item.getId(), orderItem);
                latch.countDown();
            });
        }

        latch.await();

        Item item2 = itemRepository.findById(item.getId()).orElseThrow();

        int expectedStock = 0;
        int actualStock = item2.getStock();
        assertNotEquals(expectedStock, actualStock);
    }
    @Disabled("실행 시 무한로딩")
    @DisplayName(value = "기존코드에 동시성 적용시키기 ")
    @Test
    public void 동시에_100명이_주문7() throws InterruptedException {
        int threadCount = 100;
        List<OrderItem> orderItemList = new ArrayList<>();
        Item item = new Item("ticket", 20000, 100);
        itemRepository.save(item);

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                OrderItem orderItem = new OrderItem(item, 1, 60000);
                orderItemRepository.save(orderItem);

                List<Long> orderItemIdList = new ArrayList<>();
                orderItemIdList.add(orderItem.getId());

                orderService.validate(orderItemIdList, orderItemList);

                latch.countDown();
            });
        }

        latch.await();

        Item findItem = itemRepository.findById(item.getId()).orElseThrow(() -> new ItemNotFoundException());

        assertEquals(0, findItem.getStock());
    }


    private OrderResponse orderSave() {
        List<Long> orderItemIdList = new ArrayList<>();
        orderItemIdList.add(1L);

        OrderRequest orderRequest = OrderRequest.builder()
                .memberId(1L)
                .orderItemIdList(orderItemIdList)
                .address("seoul")
                .orderDate(LocalDateTime.now())
                .build();

        OrderResponse orderResponse = orderService.placeOrder(orderRequest);
        return orderResponse;
    }
}
