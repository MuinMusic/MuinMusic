package com.mucompany.muinmusic.app;

import com.mucompany.muinmusic.Item.domain.Item;
import com.mucompany.muinmusic.Item.repository.ItemRepository;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.repository.MemberRepository;
import com.mucompany.muinmusic.orderItem.domain.OrderItem;
import com.mucompany.muinmusic.orderItem.repository.OrderItemRepository;
import com.mucompany.muinmusic.orderStatus.OrderStatus;
import com.mucompany.muinmusic.orders.api.AddOrderRequestDto;
import com.mucompany.muinmusic.orders.api.OrderResponseDto;
import com.mucompany.muinmusic.orders.app.OrdersService;
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
    private OrdersService ordersService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @DisplayName(value ="addOrderRequestDto 값 유효하면 주문 저장 후 OrderResponseDto 반환 성공" )
    @Test
    void t1() {
        AddOrderRequestDto addOrderRequestDto = createAddOrderRequestDto();

        OrderResponseDto orderResponseDto = ordersService.save(addOrderRequestDto);

        assertThat(orderResponseDto.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED.toString());
        assertThat(orderResponseDto.getOrderItems().size()).isEqualTo(2);
        assertThat(orderResponseDto.getAddress()).isEqualTo(addOrderRequestDto.getMember().getAddress());
    }

    private  AddOrderRequestDto createAddOrderRequestDto() {
        Member member = new Member("dp", "seoul");
        Item item = new Item("jpaBook", 20000, 10);
        Item item2 = new Item("springBook", 20000, 10);
        OrderItem orderItem = new OrderItem(item, 3, 60000);
        OrderItem orderItem2 = new OrderItem(item2, 1, 20000);

        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);
        orderItemList.add(orderItem2);

        Member saveMember = memberRepository.save(member);
        itemRepository.save(item);
        itemRepository.save(item2);
        orderItemRepository.save(orderItem);
        orderItemRepository.save(orderItem2);


        return AddOrderRequestDto.builder()
                .member(saveMember)
                .orderItems(orderItemList)
                .orderStatus(OrderStatus.PAYMENT_COMPLETED.toString())
                .address(member.getAddress())
                .orderDate(LocalDateTime.now())
                .build();
    }
}