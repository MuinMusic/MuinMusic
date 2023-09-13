package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.cart.domain.Cart;
import com.mucompany.muinmusic.cart.domain.CartItem;
import com.mucompany.muinmusic.cart.domain.repository.CartItemRepository;
import com.mucompany.muinmusic.cart.domain.repository.CartRepository;
import com.mucompany.muinmusic.exception.ItemNotFoundException;
import com.mucompany.muinmusic.exception.OrderNotFoundException;
import com.mucompany.muinmusic.item.domain.Item;
import com.mucompany.muinmusic.item.repository.ItemRepository;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.repository.MemberRepository;
import com.mucompany.muinmusic.order.api.OrderDto;
import com.mucompany.muinmusic.order.domain.Order;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.domain.OrderStatus;
import com.mucompany.muinmusic.order.domain.repository.OrderItemRepository;
import com.mucompany.muinmusic.order.domain.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
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
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private void h2ResetAutoIncrement() {
        jdbcTemplate.execute("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE orders ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE item ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE order_item ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE cart ALTER COLUMN id RESTART WITH 1");
    }

    @BeforeEach
    void setup() {
        h2ResetAutoIncrement();

        Member member = new Member("dp", "seoul");
        memberRepository.save(member);

        Item item = new Item("ticket1", 20000, 100);
        Item item2 = new Item("ticket2", 20000, 100);
        Item item3 = new Item("ticket3", 20000, 100);
        itemRepository.save(item);
        itemRepository.save(item2);
        itemRepository.save(item3);

        CartItem cartItem = new CartItem(item.getId(), 1, 60000);
        CartItem cartItem2 = new CartItem(item2.getId(), 1, 60000);
        CartItem cartItem3 = new CartItem(item3.getId(), 1, 60000);
        cartItemRepository.save(cartItem);
        cartItemRepository.save(cartItem2);
        cartItemRepository.save(cartItem3);

        List<CartItem> cartItems = List.of(cartItem, cartItem2, cartItem3);
        Cart cart = new Cart(member, cartItems);
        cartRepository.save(cart);
    }

    @AfterEach
    void deleteAll() {
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        cartItemRepository.deleteAll();
        itemRepository.deleteAll();
        memberRepository.deleteAll();
        orderItemRepository.deleteAll();
    }

    @Transactional
    @DisplayName(value = "orderId, memberId 값 유효하면 취소 성공 ")
    @Test
    void t1() {
        orderSave();

        Order order = orderRepository.findById(1L).orElseThrow(OrderNotFoundException::new);

        orderService.cancel(order.getId(), order.getMember().getId());

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Transactional
    @DisplayName("주문내역 논리삭제 성공 테스트")
    @Test
    void t2() {
        orderSave();

        Order order = orderRepository.findById(1L).orElseThrow();
        Member member = memberRepository.findById(order.getMember().getId()).orElseThrow();

        orderService.delete(order.getId(), member.getId());

        assertThat(order.isDelete()).isEqualTo(true);
    }

    @Transactional
    @DisplayName("3개의 주문중 삭제 되지 않은 2개의 주문목록 가져오기")
    @Test
    void t3() {
        //3개 주문
        orderSave();
        orderSave();
        orderSave();

        //마지막 주문 삭제
        Order order3 = orderRepository.findById(3L).orElseThrow();
        order3.softDelete();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

        List<OrderDto> orderHistory = orderService.getOrderHistory(1L, pageRequest);

        assertThat(orderHistory.size()).isEqualTo(2);
    }

    @DisplayName(value = "pessimisticLock 적용, 동시성 성공")
    @Test
    public void t4() throws InterruptedException {
        int threadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {

                try {
                    orderService.placeOrder(new OrderRequest(1L, 1L, "seoul", LocalDateTime.now()));

                } catch (Exception e) {
                    log.info(e.getMessage());
                }
                latch.countDown();
            });
        }

        latch.await();

        Item findItem = itemRepository.findById(1L).orElseThrow(ItemNotFoundException::new);

        assertEquals(0, findItem.getStock());
    }

    @Transactional
    @DisplayName("취소한 주문목록 가져오기")
    @Test
    void t5() {
        //3개 주문
        orderSave();
        orderSave();
        orderSave();

        //주문 취소
        Order order1 = orderRepository.findById(1L).orElseThrow();
        Order order2 = orderRepository.findById(2L).orElseThrow();
        order2.cancel();
        order1.cancel();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

        List<OrderDto> orderHistory = orderService.getCancelOrderHistory(1L, pageRequest);

        assertThat(orderHistory.size()).isEqualTo(2);
    }

    @Transactional
    @DisplayName("취소한 주문목록 없을 경우")
    @Test
    void t6() {
        //3개 주문
        orderSave();
        orderSave();
        orderSave();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

        List<OrderDto> orderHistory = orderService.getCancelOrderHistory(1L, pageRequest);

        assertThat(orderHistory.size()).isEqualTo(0);
    }

    @Transactional
    @DisplayName("주문취소시 아이템 재고 원상복구")
    @Test
    void t7() {
        OrderRequest orderRequest = OrderRequest.builder()
                .memberId(1L)
                .cartId(1L)
                .address("seoul")
                .orderDate(LocalDateTime.now())
                .build();

        orderService.placeOrder(orderRequest);

        orderService.cancel(1L, 1L);

        Item item = itemRepository.findById(1L).orElseThrow();
        Item item2 = itemRepository.findById(2L).orElseThrow();
        Item item3 = itemRepository.findById(3L).orElseThrow();

        assertThat(item.getStock()).isEqualTo(100);
        assertThat(item2.getStock()).isEqualTo(100);
        assertThat(item3.getStock()).isEqualTo(100);
    }

    @Transactional
    @DisplayName("부분 취소 테스트 성공")
    @Test
    void t8() {
        orderSave();

        List<Long> itemIdList = Arrays.asList(1L, 2L);

        orderService.partialCancel(1L, 1L, itemIdList);

        Order order = orderRepository.findById(1L).orElseThrow();

        List<OrderItem> orderItems = order.getOrderItems();

        assertThat(orderItems.get(0).getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(orderItems.get(1).getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(orderItems.get(2).getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
    }

    void orderSave() {
        Member member = memberRepository.findById(1L).orElseThrow();
        Cart cart = cartRepository.findById(1L).orElseThrow();

        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(OrderItem::new)
                .peek(orderItemRepository::save)
                .toList();

        Order order = Order.builder()
                .member(member)
                .orderItems(orderItems)
                .address("seoul")
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.ORDERED)
                .build();

        orderRepository.save(order);
    }
}
