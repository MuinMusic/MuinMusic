package com.mucompany.muinmusic.order.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mucompany.muinmusic.Item.domain.Item;
import com.mucompany.muinmusic.Item.repository.ItemRepository;
import com.mucompany.muinmusic.exception.ItemNotFoundException;
import com.mucompany.muinmusic.exception.MemberNotFoundException;
import com.mucompany.muinmusic.exception.OrderItemNotFoundException;
import com.mucompany.muinmusic.exception.OutOfStockException;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.domain.OrderStatus;
import com.mucompany.muinmusic.order.domain.repository.OrderItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
@Transactional
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

    @DisplayName(value = "orderRequestDto값 유효하면 http응답 201, 객체 반환 성공")
    @Test
    void t1() throws Exception {
        OrderRequestDto orderRequestDto = createOrderRequestDto();

        String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(orderRequestDto);

        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$..memberId").value(1))
                .andExpect(jsonPath("$..orderItemIdList").isNotEmpty())
                .andExpect(jsonPath("$..orderStatus").value(orderRequestDto.getOrderStatus()))
                .andExpect(jsonPath("$..address").value(orderRequestDto.getAddress()))
                .andExpect(jsonPath("$..orderDate").exists());
    }

    @DisplayName(value = "orderRequestDto.memberId로 member 찾지 못할 경우 MemberNotFoundException 발생 및 HTTP404 응답")
    @Test
    void t2() throws Exception {
        OrderRequestDto orderRequestDto = createOrderRequestDto();

        orderRequestDto.setMemberId(100L);

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

    @DisplayName(value = "orderRequestDto.orderItemIdList 로 orderItem 찾지 못할 경우 OrderItemNotFoundException 발생 및 HTTP404 응답")
    @Test
    void t3() throws Exception {
        OrderRequestDto orderRequestDto = createOrderRequestDto();

        List<Long> orderItemIdList = List.of(100L, 200L);

        orderRequestDto.setOrderItemIdList(orderItemIdList);

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

    @DisplayName(value = "주문하려는 Item 상품 없을 시 ItemNotFoundException 발생 및 HTTP404 응답")
    @Test
    void t4() throws Exception {
        OrderRequestDto orderRequestDto = createOrderRequestDto();

        List<Long> orderItemIdList = orderRequestDto.getOrderItemIdList();
        List<Item> item = new ArrayList<>();
        for (Long orderItemId : orderItemIdList) {
            OrderItem result = orderItemRepository.findById(orderItemId).orElseThrow(OrderItemNotFoundException::new);
            item.add(result.getItem());
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

    @DisplayName(value = "주문하려는 Item의 재고수량 초과했을경우 OutOfStockException 발생 및 HTTP404 응답")
    @Test
    void t5() throws Exception {
        OrderRequestDto orderRequestDto = createOrderRequestDto();
        //아이템의 수량은 5개로 셋팅해놨다
        Item book = new Item("개구리책", 20000, 5);
        itemRepository.save(book);
        //6개를 주문한다면
        OrderItem orderItem = new OrderItem(book, 6, 120000);
        orderItemRepository.save(orderItem);

        List<Long> orderItemIdList = List.of(orderItem.getId());
        orderRequestDto.setOrderItemIdList(orderItemIdList);

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

    private OrderRequestDto createOrderRequestDto() {
        Member member = new Member("dp", "seoul");
        Item item = new Item("jpaBook", 20000, 10);
        Item item2 = new Item("springBook", 20000, 10);
        OrderItem orderItem = new OrderItem(item, 3, 60000);
        OrderItem orderItem2 = new OrderItem(item2, 1, 20000);

        Member saveMember = memberRepository.save(member);
        itemRepository.save(item);
        itemRepository.save(item2);
        orderItemRepository.save(orderItem);
        orderItemRepository.save(orderItem2);

        List<Long> orderItemIdList = List.of(orderItem.getId(), orderItem2.getId());

        return OrderRequestDto.builder()
                .memberId(saveMember.getId())
                .orderItemIdList(orderItemIdList)
                .orderStatus(OrderStatus.PAYMENT_COMPLETED.toString())
                .address(member.getAddress())
                .orderDate(LocalDateTime.now())
                .build();
    }
}
