package com.mucompany.muinmusic.repository;

import com.mucompany.muinmusic.Item.domain.Item;
import com.mucompany.muinmusic.Item.repository.ItemRepository;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.repository.MemberRepository;
import com.mucompany.muinmusic.orderItem.domain.OrderItem;
import com.mucompany.muinmusic.orderItem.repository.OrderItemRepository;
import com.mucompany.muinmusic.orderstautus.OrderStatus;
import com.mucompany.muinmusic.orders.domain.Orders;
import com.mucompany.muinmusic.orders.api.AddOrderRequestDto;
import com.mucompany.muinmusic.orders.repository.OrdersRepository;
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
public class OrderRepositoryTest {

    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    OrderItemRepository orderItemRepository;

    @DisplayName(value ="addOrderRequestDto 값 유효하면 저장 성공" )
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


        AddOrderRequestDto addOrderRequestDto = AddOrderRequestDto.builder()
                .member(member)
                .orderItems(orderItemList)
                .orderStatus(OrderStatus.PAYMENT_COMPLETED.toString())
                .address(member.getAddress())
                .orderDate(LocalDateTime.now())
                .build();
        Orders orders = new Orders(addOrderRequestDto);

        Orders saveOrder = ordersRepository.save(orders);
        assertThat(saveOrder.getOrderItems().size()).isEqualTo(2);
        assertThat(saveOrder.getMember().getName()).isEqualTo(member.getName());
    }
}