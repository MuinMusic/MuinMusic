package com.mucompany.muinmusic.order.repository;

import com.mucompany.muinmusic.Item.domain.Item;
import com.mucompany.muinmusic.Item.repository.ItemRepository;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.domain.OrderStatus;
import com.mucompany.muinmusic.order.domain.Order;
import com.mucompany.muinmusic.order.domain.repository.OrderItemRepository;
import com.mucompany.muinmusic.order.domain.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
//@ActiveProfiles("test")
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @DisplayName(value = "addOrderRequestDto 값 유효하면 저장 성공")
    @Test
    void t1() {

        Member member = new Member("dp", "seoul");
        Item item = new Item("jpaBook", 20000, 10);
        Item item2 = new Item("springBook", 20000, 10);
        OrderItem orderItem = new OrderItem(item, 3, 60000);
        OrderItem orderItem2 = new OrderItem(item2, 1, 20000);

        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);
        orderItemList.add(orderItem2);

        memberRepository.save(member);
        itemRepository.save(item);
        itemRepository.save(item2);
        orderItemRepository.save(orderItem);
        orderItemRepository.save(orderItem2);

        Order order = Order.builder()
                .member(member)
                .orderItems(orderItemList)
                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                .address(member.getAddress())
                .orderDate(LocalDateTime.now())
                .build();

        Order saveOrder = orderRepository.save(order);
        Assertions.assertThat(saveOrder.getOrderItems().size()).isEqualTo(2);
        Assertions.assertThat(saveOrder.getMember().getName()).isEqualTo(member.getName());
    }
}
