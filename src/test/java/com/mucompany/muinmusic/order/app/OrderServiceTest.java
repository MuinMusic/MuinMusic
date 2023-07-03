package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.cart.domain.Cart;
import com.mucompany.muinmusic.cart.domain.CartItem;
import com.mucompany.muinmusic.cart.domain.repository.CartItemRepository;
import com.mucompany.muinmusic.cart.domain.repository.CartRepository;
import com.mucompany.muinmusic.item.domain.Item;
import com.mucompany.muinmusic.item.repository.ItemRepository;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    }

    @BeforeEach
    void setup() {
        h2ResetAutoIncrement();

        Member member = new Member("dp", "seoul");
        memberRepository.save(member);

        Item item = new Item("jpaBook1", 20000, 100);
        Item item2 = new Item("jpaBook2", 20000, 100);
        Item item3 = new Item("jpaBook3", 20000, 100);
        itemRepository.save(item);
        itemRepository.save(item2);
        itemRepository.save(item3);

        CartItem cartItem = new CartItem(item.getId(), 1, 60000);
        CartItem cartItem2 = new CartItem(item2.getId(), 1, 60000);
        CartItem cartItem3 = new CartItem(item3.getId(), 1, 60000);

        cartItemRepository.save(cartItem);
        cartItemRepository.save(cartItem2);
        cartItemRepository.save(cartItem3);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        cartItems.add(cartItem2);
        cartItems.add(cartItem3);

        Cart cart = new Cart(member,cartItems);
        cartRepository.save(cart);
    }

    @AfterEach
    void deleteAll() {
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        cartItemRepository.deleteAll();
        itemRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Transactional
    @DisplayName(value = "orderId, memberId 값 유효하면 취소 성공 ")
    @Test
    void t1() {
        orderSave();

        Order order = orderRepository.findById(1L).orElseThrow();

        orderService.cancel(order.getId(), order.getMember().getId());

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

     void orderSave() {
        Member member = memberRepository.findById(1L).orElseThrow();
        Cart cart = cartRepository.findById(1L).orElseThrow();
        List<CartItem> cartItems = cart.getCartItems();

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(cartItem);
            orderItemRepository.save(orderItem);
            orderItems.add(orderItem);
        }

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
