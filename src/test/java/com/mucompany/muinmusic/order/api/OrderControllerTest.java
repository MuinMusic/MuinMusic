package com.mucompany.muinmusic.order.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mucompany.muinmusic.Item.domain.Item;
import com.mucompany.muinmusic.Item.repository.ItemRepository;
import com.mucompany.muinmusic.exception.*;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
import com.mucompany.muinmusic.order.app.OrderRequest;
import com.mucompany.muinmusic.order.app.OrderResponse;
import com.mucompany.muinmusic.order.app.OrderService;
import com.mucompany.muinmusic.order.domain.Order;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.domain.OrderStatus;
import com.mucompany.muinmusic.order.domain.repository.OrderItemRepository;
import com.mucompany.muinmusic.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
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

        memberRepository.save(member);
        itemRepository.save(item);
        itemRepository.save(item2);

        OrderItem orderItem = new OrderItem(item.getId(), 3, 60000);
        OrderItem orderItem2 = new OrderItem(item2.getId(), 1, 20000);
        orderItemRepository.save(orderItem);
        orderItemRepository.save(orderItem2);
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
                .andExpect(jsonPath("$..address").value(orderRequestDto.getAddress()))
                .andExpect(jsonPath("$..orderDate").exists());
    }

    @Transactional
    @DisplayName(value = "orderRequestDto.memberId로 member 찾지 못할 경우 MemberNotFoundException 발생 및 HTTP404 응답")
    @Test
    void t2() throws Exception {
        List<Long> orderItemIdList = List.of(1L, 2L);

        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .memberId(-1L)
                .orderItemIdList(orderItemIdList)
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
    @DisplayName(value = "orderRequestDto.orderItemIdList 로 orderItem 찾지 못할 경우 OrderItemNotFoundException 발생 및 HTTP404 응답")
    @Test
    void t3() throws Exception {
        List<Long> orderItemIdList = List.of(-1L, -2L);

        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .memberId(1L)
                .orderItemIdList(orderItemIdList)
                .address("seoul")
                .orderDate(LocalDateTime.now())
                .build();

        String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(orderRequestDto);

        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    Throwable exception = result.getResolvedException();
                    assertNotNull(exception);
                    assertEquals(OrderItemNotFoundException.class, exception.getClass());
                    assertEquals("주문 상품을 찾을 수 없습니다", exception.getMessage());
                });
    }

    @Transactional
    @DisplayName(value = "주문하려는 Item 상품 없을 시 ItemNotFoundException 발생 및 HTTP404 응답")
    @Test
    void t4() throws Exception {
        OrderRequestDto orderRequestDto = createOrderRequestDto();

        List<Long> orderItemIdList = orderRequestDto.getOrderItemIdList();
        List<Item> item = new ArrayList<>();
        for (Long orderItemId : orderItemIdList) {
            OrderItem result = orderItemRepository.findById(orderItemId).orElseThrow(OrderItemNotFoundException::new);
            Long itemId = result.getItemId();
            Item findItem = itemRepository.findById(itemId).orElseThrow();
            item.add(findItem);
        }
        itemRepository.delete(item.get(1));

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
    @DisplayName(value = "주문하려는 Item의 재고수량 초과했을경우 OutOfStockException 발생 및 HTTP404 응답")
    @Test
    void t5() throws Exception {
        //아이템의 수량은 5개로 셋팅해놨다
        Item book = new Item("개구리책", 20000, 5);
        itemRepository.save(book);
        //6개를 주문한다면
        OrderItem orderItem = new OrderItem(book.getId(), 6, 120000);
        orderItemRepository.save(orderItem);

        List<Long> orderItemIdList = List.of(orderItem.getId());

        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .memberId(1L)
                .orderItemIdList(orderItemIdList)
                .address("seoul")
                .orderDate(LocalDateTime.now())
                .build();

        String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(orderRequestDto);

        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    Throwable exception = result.getResolvedException();
                    assertNotNull(exception);
                    assertEquals(OutOfStockException.class, exception.getClass());
                    assertEquals("상품의 재고가 부족합니다", exception.getMessage());
                });
    }

    @Transactional
    @DisplayName(value = "orderId,userId 값 유효하면 주문 취소 성공")
    @Test
    void t6() throws Exception {
        OrderResponse orderResponse = orderPlace();

        Long orderItemId = orderResponse.getOrderItemIdList().get(0);
        Order order = orderRepository.findByOrderItemsId(orderItemId);

        Long orderId = order.getId();
        Long memberId = orderResponse.getMemberId();

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

        Long orderItemId = orderResponse.getOrderItemIdList().get(1);
        Order order = orderRepository.findByOrderItemsId(orderItemId);

        Long orderId = order.getId();
        Long memberId = otherMember.getId();

        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId).param("memberId", memberId.toString()))
                .andExpect(status().isNotFound())
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

        Long orderItemId = orderResponse.getOrderItemIdList().get(0);
        orderRepository.findByOrderItemsId(orderItemId);

        Long orderId = -1L;
        Long memberId = orderResponse.getMemberId();

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

        Long orderItemId = orderResponse.getOrderItemIdList().get(0);
        Order order = orderRepository.findByOrderItemsId(orderItemId);

        order.shipping();

        Long orderId = order.getId();
        Long memberId = orderResponse.getMemberId();

        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId).param("memberId", memberId.toString()))
                .andExpect(status().isConflict())
                .andExpect(result -> {
                    Throwable exception = result.getResolvedException();
                    assertNotNull(exception);
                    assertEquals(OrderCancellationException.class, exception.getClass());
                    assertEquals("배송중인 상품은 취소할 수 없습니다.", exception.getMessage());
                });
    }

    private OrderResponse orderPlace() {
        List<Long> orderItemIdList = List.of(1L, 2L);
        OrderRequest orderRequest = OrderRequest.builder()
                .memberId(1L)
                .orderItemIdList(orderItemIdList)
                .address("seoul")
                .orderDate(LocalDateTime.now())
                .build();

        OrderResponse orderResponse = orderService.placeOrderWithRedissonLock(orderRequest);

        return orderResponse;
    }

    private OrderRequestDto createOrderRequestDto() {
        List<Long> orderItemIdList = List.of(1L, 2L);
        Member member = memberRepository.findById(1L).orElseThrow(MemberNotFoundException::new);

        return OrderRequestDto.builder()
                .memberId(1L)
                .orderItemIdList(orderItemIdList)
                .address(member.getAddress())
                .orderDate(LocalDateTime.now())
                .build();
    }
}
