package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.Item.domain.Item;
import com.mucompany.muinmusic.Item.repository.ItemRepository;
import com.mucompany.muinmusic.exception.ItemNotFoundException;
import com.mucompany.muinmusic.exception.MemberNotFoundException;
import com.mucompany.muinmusic.exception.OrderItemNotFoundException;
import com.mucompany.muinmusic.exception.OutOfStockException;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.repository.MemberRepository;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.repository.OrderItemRepository;
import com.mucompany.muinmusic.order.api.OrderRequestDto;
import com.mucompany.muinmusic.order.api.OrderResponseDto;
import com.mucompany.muinmusic.order.domain.Order;
import com.mucompany.muinmusic.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public OrderResponseDto save(OrderRequestDto orderRequestDto) {
        //회원 유효한지 체크
        Member member = memberRepository.findById(orderRequestDto.getMemberId()).orElseThrow(MemberNotFoundException::new);

        //주문 아이템 존재여부 체크
        List<Long> orderItemIdList = orderRequestDto.getOrderItemIdList();
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemCheck(orderItemIdList, orderItemList);

        Order order = createOrder(orderRequestDto, member, orderItemList);

        orderRepository.save(order);

        return createOrderResponseDto(orderRequestDto, member, orderItemIdList);
    }

    private void orderItemCheck(List<Long> orderItemIdList, List<OrderItem> orderItemList) {
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(OrderItemNotFoundException::new);

            Item item = itemRepository.findById(orderItem.getItem().getId()).orElseThrow(ItemNotFoundException::new);

            //재고 수량 파악
            if (item.getStock() < orderItem.getCount()) {
                throw new OutOfStockException();
            }
            orderItemList.add(orderItem);
        }
    }

    private static Order createOrder(OrderRequestDto orderRequestDto, Member member, List<OrderItem> orderItemList) {
        return Order.builder()
                .member(member)
                .orderItems(orderItemList)
                .orderStatus(orderRequestDto.getOrderStatus())
                .address(member.getAddress())
                .orderDate(orderRequestDto.getOrderDate())
                .build();
    }

    private static OrderResponseDto createOrderResponseDto(OrderRequestDto orderRequestDto, Member member, List<Long> orderItemIdList) {
        return OrderResponseDto.builder()
                .memberId(member.getId())
                .orderItemIdList(orderItemIdList)
                .orderStatus(orderRequestDto.getOrderStatus())
                .address(member.getAddress())
                .orderDate(orderRequestDto.getOrderDate())
                .build();
    }
}