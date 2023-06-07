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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
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

    @DisplayName(value = "OrderRequest 값 유효하면 주문 저장 성공 및 OrderResponse 반환 성공")
    @Test
    void t1() {
        OrderRequest orderRequest = createConvertOrderDto();

        OrderResponse orderResponse = orderService.placeOrder(orderRequest);

        Order order = orderRepository.findById(orderResponse.getMemberId()).orElseThrow(MemberNotFoundException::new);

        assertThat(order.getOrderStatus()).isEqualTo(orderResponse.getOrderStatus());
        assertThat(order.getMember().getAddress()).isEqualTo(orderResponse.getAddress());
        assertThat(order.getOrderItems().size()).isEqualTo(orderResponse.getOrderItemIdList().size());
    }

    private OrderRequest createConvertOrderDto() {
        Member member = new Member("dp", "seoul");
        Item item = new Item("jpaBook", 20000, 10);
        Item item2 = new Item("springBook", 20000, 10);
        OrderItem orderItem = new OrderItem(item, 3, 60000);
        OrderItem orderItem2 = new OrderItem(item2, 1, 20000);

        List<Long> orderItemIdList = new ArrayList<>();

        Member saveMember = memberRepository.save(member);
        itemRepository.save(item);
        itemRepository.save(item2);
        orderItemRepository.save(orderItem);
        orderItemRepository.save(orderItem2);

        orderItemIdList.add(orderItem.getId());
        orderItemIdList.add(orderItem2.getId());

        return OrderRequest.builder()
                .memberId(saveMember.getId())
                .orderItemIdList(orderItemIdList)
                .address(member.getAddress())
                .orderDate(LocalDateTime.now())
                .build();
    }
}
