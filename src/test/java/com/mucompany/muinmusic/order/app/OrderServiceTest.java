package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.Item.domain.Item;
import com.mucompany.muinmusic.Item.repository.ItemRepository;
import com.mucompany.muinmusic.exception.MemberNotFoundException;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
import com.mucompany.muinmusic.order.domain.Order;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.domain.OrderStatus;
import com.mucompany.muinmusic.order.domain.repository.OrderItemRepository;
import com.mucompany.muinmusic.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        Item item = new Item("jpaBook", 20000, 10);
        Item item2 = new Item("springBook", 20000, 10);
        OrderItem orderItem = new OrderItem(item, 3, 60000);
        OrderItem orderItem2 = new OrderItem(item2, 1, 20000);

        memberRepository.save(member);
        itemRepository.save(item);
        itemRepository.save(item2);
        orderItemRepository.save(orderItem);
        orderItemRepository.save(orderItem2);
    }

    @DisplayName(value = "OrderRequest 값 유효하면 주문 저장 성공 및 OrderResponse 반환 성공")
    @Test
    void t1() {
        //이메일로 멤버 찾기 unique
        List<Long> orderItemIdList = new ArrayList<>();

        orderItemIdList.add(1L);

        OrderRequest orderRequest = OrderRequest.builder()
                .memberId(1L)
                .orderItemIdList(orderItemIdList)
                .address("seoul")
                .orderDate(LocalDateTime.now())
                .build();

        OrderResponse orderResponse = orderService.placeOrder(orderRequest);

        Order order = orderRepository.findById(orderResponse.getMemberId()).orElseThrow(MemberNotFoundException::new);

        assertThat(order.getOrderStatus()).isEqualTo(orderResponse.getOrderStatus());
        assertThat(order.getMember().getAddress()).isEqualTo(orderResponse.getAddress());
        assertThat(order.getOrderItems().size()).isEqualTo(orderResponse.getOrderItemIdList().size());
    }

    @DisplayName(value = "orderId, memberId 값 유효하면 취소 성공 ")
    @Test
    void t2() {
        OrderItem orderItem = orderItemRepository.findAll().get(0);
        List<Long> orderItemIdList = new ArrayList<>();
        orderItemIdList.add(orderItem.getId());

        OrderRequest orderRequest = OrderRequest.builder()
                .memberId(1L)
                .orderItemIdList(orderItemIdList)
                .address("seoul")
                .orderDate(LocalDateTime.now())
                .build();

        OrderResponse orderResponse = orderService.placeOrder(orderRequest);
        Long orderItemId = orderResponse.getOrderItemIdList().get(0);

        Order order = orderRepository.findByOrderItemsId(orderItemId);
        Long memberId = orderResponse.getMemberId();

        orderService.cancel(order.getId(), memberId);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
}
