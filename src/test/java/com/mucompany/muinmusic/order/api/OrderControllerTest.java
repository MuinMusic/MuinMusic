package com.mucompany.muinmusic.order.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mucompany.muinmusic.cart.domain.Cart;
import com.mucompany.muinmusic.cart.domain.CartItem;
import com.mucompany.muinmusic.cart.domain.repository.CartItemRepository;
import com.mucompany.muinmusic.cart.domain.repository.CartRepository;
import com.mucompany.muinmusic.exception.CartNotFoundException;
import com.mucompany.muinmusic.exception.InsufficientStockException;
import com.mucompany.muinmusic.exception.ItemNotFoundException;
import com.mucompany.muinmusic.exception.MemberNotFoundException;
import com.mucompany.muinmusic.exception.NotMatchTheOrdererException;
import com.mucompany.muinmusic.exception.OrderCancellationException;
import com.mucompany.muinmusic.exception.OrderNotFoundException;
import com.mucompany.muinmusic.exception.UnableToDeleteOrderException;
import com.mucompany.muinmusic.item.domain.Item;
import com.mucompany.muinmusic.item.repository.ItemRepository;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.repository.MemberRepository;
import com.mucompany.muinmusic.order.app.OrderRequest;
import com.mucompany.muinmusic.order.app.OrderResponse;
import com.mucompany.muinmusic.order.app.OrderService;
import com.mucompany.muinmusic.order.domain.Order;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.domain.OrderStatus;
import com.mucompany.muinmusic.order.domain.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private OrderService orderService;


    private void h2ResetAutoIncrement() {
        jdbcTemplate.execute("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE orders ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE item ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE order_item ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE cart ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE cart_item ALTER COLUMN id RESTART WITH 1");
    }

    @BeforeEach
    void setup() {
        h2ResetAutoIncrement();

        Member member = new Member("dp", "seoul");
        memberRepository.save(member);

        Item item = new Item("ticket1", 20000, 100);
        Item item2 = new Item("ticket", 20000, 100);
        Item item3 = new Item("ticket3", 20000, 100);
        List<Item> items = List.of(item, item2, item3);
        itemRepository.saveAll(items);

        CartItem cartItem = new CartItem(item.getId(), 1, 60000);
        CartItem cartItem2 = new CartItem(item2.getId(), 1, 60000);
        CartItem cartItem3 = new CartItem(item3.getId(), 1, 60000);
        List<CartItem> cartItems = List.of(cartItem, cartItem2, cartItem3);
        cartItemRepository.saveAll(cartItems);

        Cart cart = new Cart(member, cartItems);
        cartRepository.save(cart);
    }

    @AfterEach
    void deleteAll() {
        orderRepository.deleteAllInBatch();
        cartRepository.deleteAllInBatch();
        cartItemRepository.deleteAllInBatch();
        itemRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }


    @Transactional
    @DisplayName(value = "orderRequestDto값 유효하면 http응답 201, 객체 반환 성공")
    @Test
    void t1() throws Exception {
        OrderRequestDto orderRequestDto = createOrderRequestDto();

        String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(orderRequestDto);

        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$..memberId").value(1))
                .andExpect(jsonPath("$..orderItemIdList").isNotEmpty())
                .andExpect(jsonPath("$..orderStatus").value(OrderStatus.PAYMENT_COMPLETED.toString()))
                .andExpect(jsonPath("$..address").value(orderRequestDto.address()))
                .andExpect(jsonPath("$..orderDate").exists());
    }

    @Transactional
    @DisplayName(value = "orderRequestDto.memberId로 member 찾지 못할 경우 MemberNotFoundException 발생 및 HTTP404 응답")
    @Test
    void t2() throws Exception {

        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .memberId(-1L)
                .cartId(1L)
                .address("seoul")
                .orderDate(LocalDateTime.now())
                .build();

        String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(orderRequestDto);

        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    Throwable exception = result.getResolvedException();
                    assertNotNull(exception);
                    assertEquals(MemberNotFoundException.class, exception.getClass());
                    assertEquals("회원을 찾을 수 없습니다", exception.getMessage());
                });
    }

    @Transactional
    @DisplayName(value = "orderRequestDto.cartId 로 cartItem 찾지 못할 경우 CartItemNotFoundException 발생 및 HTTP404 응답")
    @Test
    void t3() throws Exception {
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .memberId(1L)
                .cartId(-1L)
                .address("seoul")
                .orderDate(LocalDateTime.now())
                .build();

        String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(orderRequestDto);

        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    Throwable exception = result.getResolvedException();
                    assertNotNull(exception);
                    assertEquals(CartNotFoundException.class, exception.getClass());
                    assertEquals("장바구니를 불러 올 수 없습니다", exception.getMessage());
                });
    }

    @Transactional
    @DisplayName(value = "주문하려는 Item 상품 없을 시 ItemNotFoundException 발생 및 HTTP404 응답")
    @Test
    void t4() throws Exception {
        OrderRequestDto orderRequestDto = createOrderRequestDto();
//
        Long cartId = orderRequestDto.cartId();
        Cart cart = cartRepository.findById(cartId).orElseThrow();

        List<Item> items = cart.getCartItems().stream()
                .map(cartItem -> itemRepository.findById(cartItem.getItemId()).orElseThrow(ItemNotFoundException::new))
                .toList();

        itemRepository.delete(items.get(0));

        String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(orderRequestDto);

        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    Throwable exception = result.getResolvedException();
                    assertNotNull(exception);
                    assertEquals(ItemNotFoundException.class, exception.getClass());
                    assertEquals("상품을 찾을 수 없습니다", exception.getMessage());
                });
    }

    @Transactional
    @DisplayName(value = "주문하려는 Item의 재고수량 초과했을경우 InsufficientStockException 발생 및 HTTP409 응답")
    @Test
    void t5() throws Exception {
        Member member = new Member("Dongpil Son", "seoul");
        memberRepository.save(member);

        //아이템의 수량은 5개로 셋팅해놨다
        Item ticket = new Item("xxx콘서트 티켓", 20000, 5);
        itemRepository.save(ticket);

        //6개를 주문한다면
        CartItem cartItem = new CartItem(ticket.getId(), 6, 120000);
        List<CartItem> cartItems = List.of(cartItem);
        cartItemRepository.saveAll(cartItems);

        Cart cart = new Cart(member, cartItems);
        cartRepository.save(cart);

        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .memberId(member.getId())
                .cartId(cart.getId())
                .address("seoul")
                .orderDate(LocalDateTime.now())
                .build();

        String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(orderRequestDto);

        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(result -> {
                    Throwable exception = result.getResolvedException();
                    assertNotNull(exception);
                    assertEquals(InsufficientStockException.class, exception.getClass());
                    assertEquals("상품의 재고가 부족합니다", exception.getMessage());
                });
    }

    @Transactional
    @DisplayName(value = "orderId,userId 값 유효하면 주문 취소 성공")
    @Test
    void t6() throws Exception {
        OrderResponse orderResponse = orderPlace();

        List<Long> orderItemIdList = orderResponse.orderItemIdList();
        Order order = orderRepository.findByOrderItemsId(orderItemIdList.get(0));

        Long orderId = order.getId();
        Long memberId = orderResponse.memberId();

        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId).param("memberId", memberId.toString()))
                .andExpect(status().isNoContent());
    }

    @Transactional
    @DisplayName(value = "주문 취소 시 주문자와 로그인한 회원이 일치하지 않을 경우 NotMatchTheOrdererException 발생")
    @Test
    void t7() throws Exception {
        OrderResponse orderResponse = orderPlace();

        Member otherMember = new Member("새 멤버", "seoul");
        memberRepository.save(otherMember);

        Long orderItemId = orderResponse.orderItemIdList().get(1);
        Order order = orderRepository.findByOrderItemsId(orderItemId);

        Long orderId = order.getId();
        Long memberId = otherMember.getId();

        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId).param("memberId", memberId.toString()))
                .andExpect(status().isConflict())
                .andExpect(result -> {
                    Throwable exception = result.getResolvedException();
                    assertNotNull(exception);
                    assertEquals(NotMatchTheOrdererException.class, exception.getClass());
                    assertEquals("주문자와 로그인한 회원이 일치하지 않습니다.", exception.getMessage());
                });
    }

    @Transactional
    @DisplayName(value = "주문취소 시 주문한 상품을 찾을 수 없는 경우 OrderNotFoundException 발생")
    @Test
    void t8() throws Exception {
        OrderResponse orderResponse = orderPlace();

        Long orderItemId = orderResponse.orderItemIdList().get(0);
        orderRepository.findByOrderItemsId(orderItemId);

        Long orderId = -1L;
        Long memberId = orderResponse.memberId();

        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId).param("memberId", memberId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    Throwable exception = result.getResolvedException();
                    assertNotNull(exception);
                    assertEquals(OrderNotFoundException.class, exception.getClass());
                    assertEquals("주문한 상품을 찾을 수 없습니다.", exception.getMessage());
                });
    }

    @Transactional
    @DisplayName(value = "주문취소 시 이미 상품이 배송중일 경우 OrderCancellationException 발생")
    @Test
    void t9() throws Exception {
        OrderResponse orderResponse = orderPlace();

        Long orderItemId = orderResponse.orderItemIdList().get(0);

        Order order = orderRepository.findByOrderItemsId(orderItemId);
        order.shipping();

        Long orderId = order.getId();
        Long memberId = orderResponse.memberId();

        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId).param("memberId", memberId.toString()))
                .andExpect(status().isConflict())
                .andExpect(result -> {
                    Throwable exception = result.getResolvedException();
                    assertNotNull(exception);
                    assertEquals(OrderCancellationException.class, exception.getClass());
                    assertEquals("배송중인 상품은 취소할 수 없습니다.", exception.getMessage());
                });
    }

    @Transactional
    @DisplayName("orderId,memberId 일치하면 주문삭제 성공")
    @Test
    void t10() throws Exception {
        OrderResponse orderResponse = orderPlace();

        Long orderItemId = orderResponse.orderItemIdList().get(0);
        Order order = orderRepository.findByOrderItemsId(orderItemId);

        Long orderId = order.getId();
        Long memberId = orderResponse.memberId();

        mockMvc.perform(delete("/api/orders/{orderId}", orderId).param("memberId", memberId.toString()))
                .andExpect(status().isNoContent());
    }

    @Transactional
    @DisplayName("배송중일경우 주문삭제 실패")
    @Test
    void t11() throws Exception {
        OrderResponse orderResponse = orderPlace();

        Long orderItemId = orderResponse.orderItemIdList().get(0);
        Order order = orderRepository.findByOrderItemsId(orderItemId);

        order.shipping();

        Long orderId = order.getId();
        Long memberId = orderResponse.memberId();

        mockMvc.perform(delete("/api/orders/{orderId}", orderId).param("memberId", memberId.toString()))
                .andExpect(status().isConflict())
                .andExpect(result -> {
                    Throwable exception = result.getResolvedException();
                    assertNotNull(exception);
                    assertEquals(UnableToDeleteOrderException.class, exception.getClass());
                    assertEquals("주문내역을 삭제할 수 없습니다", exception.getMessage());
                });
    }

    @Transactional
    @DisplayName("주문자와 로그인회원 다를 경우 주문삭제 실패")
    @Test
    void t12() throws Exception {
        OrderResponse orderResponse = orderPlace();

        Member otherMember = memberRepository.save(new Member("sdp", "seoul"));
        Long orderItemId = orderResponse.orderItemIdList().get(0);
        Order order = orderRepository.findByOrderItemsId(orderItemId);

        Long orderId = order.getId();
        Long memberId = otherMember.getId();

        mockMvc.perform(delete("/api/orders/{orderId}", orderId).param("memberId", memberId.toString()))
                .andExpect(status().isConflict());
    }

    @Transactional
    @DisplayName("3건의 주문내역 가져오기")
    @Test
    void t13() throws Exception {
        orderPlace();
        orderPlace();
        orderPlace();

        Long memberId = 1L;

        mockMvc.perform(get("/api/orders").param("memberId", memberId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].member.name").value("dp"))
                .andExpect(jsonPath("$[0].address").value("seoul"))
                .andExpect(jsonPath("$[0].orderItems[0].itemId").value(1))
                .andExpect(jsonPath("$[1].orderItems[1].itemId").value(2))
                .andExpect(jsonPath("$[1].orderItems[2].itemId").value(3))
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Transactional
    @DisplayName("회원 아이디 조회 안될 시 예외 및 메세지 반환")
    @Test
    void t14() throws Exception {
        orderPlace();
        orderPlace();
        orderPlace();

        Long memberId = -1L;

        mockMvc.perform(get("/api/orders").param("memberId", memberId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("회원을 찾을 수 없습니다"));
    }

    @Transactional
    @DisplayName("취소된 주문내역 가져오기")
    @Test
    void t15() throws Exception {
        orderPlace();
        orderPlace();
        orderPlace();

        Order order1 = orderRepository.findById(1L).orElseThrow();
        order1.cancel();

        Long memberId = 1L;

        mockMvc.perform(get("/api/orders/cancel").param("memberId", memberId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].member.name").value("dp"))
                .andExpect(jsonPath("$[0].address").value("seoul"))
                .andExpect(jsonPath("$[0].orderItems[0].itemId").value(1))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Transactional
    @DisplayName("취소된 주문내역 없을 경우")
    @Test
    void t16() throws Exception {
        orderPlace();
        orderPlace();
        orderPlace();

        Long memberId = 1L;

        mockMvc.perform(get("/api/orders/cancel").param("memberId", memberId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Transactional
    @DisplayName(value = "orderId,userId 값 유효하면 주문 부분취소 성공")
    @Test
    void t17() throws Exception {
        orderPlace();
        Order order = orderRepository.findById(1L).orElseThrow();

        Long orderId = 1L;
        Long memberId = 1L;
        String itemIdList = "1,2";

        mockMvc.perform(post("/api/orders/{orderId}/partial_cancel", orderId)
                        .param("memberId", memberId.toString())
                        .param("itemIdList", itemIdList))
                .andExpect(status().isNoContent());

        List<OrderItem> orderItems = order.getOrderItems();
        assertThat(orderItems).hasSize(3)
                .extracting("itemId", "orderStatus")
                .containsExactlyInAnyOrder(
                        tuple(1L,OrderStatus.CANCELLED),
                        tuple(2L,OrderStatus.CANCELLED),
                        tuple(3L,OrderStatus.PAYMENT_COMPLETED)
                );
    }

    private OrderResponse orderPlace() {
        OrderRequest orderRequest = OrderRequest.builder()
                .memberId(1L)
                .cartId(1L)
                .address("seoul")
                .orderDate(LocalDateTime.now())
                .build();

        OrderResponse orderResponse = orderService.placeOrder(orderRequest);

        return orderResponse;
    }

    private OrderRequestDto createOrderRequestDto() {
        Member member = memberRepository.findById(1L).orElseThrow(MemberNotFoundException::new);

        return OrderRequestDto.builder()
                .memberId(1L)
                .cartId(1L)
                .address(member.getAddress())
                .orderDate(LocalDateTime.now())
                .build();
    }
}
