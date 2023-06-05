package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.Item.domain.Item;
import com.mucompany.muinmusic.Item.repository.ItemRepository;
import com.mucompany.muinmusic.exception.ItemNotFoundException;
import com.mucompany.muinmusic.exception.MemberNotFoundException;
import com.mucompany.muinmusic.exception.OrderItemNotFoundException;
import com.mucompany.muinmusic.exception.OutOfStockException;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
import com.mucompany.muinmusic.order.domain.Order;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.domain.repository.OrderItemRepository;
import com.mucompany.muinmusic.order.domain.repository.OrderRepository;
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
    public ConvertOrderDto save(ConvertOrderDto convertOrderDto) {
        //회원 유효한지 체크
        Member member = memberRepository.findById(convertOrderDto.getMemberId()).orElseThrow(MemberNotFoundException::new);

        //주문 아이템 존재여부 체크
        List<Long> orderItemIdList = convertOrderDto.getOrderItemIdList();
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemCheck(orderItemIdList, orderItemList);

        Order order = createOrder(convertOrderDto, member, orderItemList);

        orderRepository.save(order);


        return convertOrderDto;
    }

    private void orderItemCheck(List<Long> orderItemIdList, List<OrderItem> orderItemList) {
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(OrderItemNotFoundException::new);

            Item item = itemRepository.findById(orderItem.getItem().getId()).orElseThrow(ItemNotFoundException::new);

            //수량 체크 및 변경 된 수량 업데이트
            if (item.getStock() < orderItem.getCount()) {
                throw new OutOfStockException();
            } else {
                int updatedStock = item.getStock() - orderItem.getCount();
                item.setStock(updatedStock);
                itemRepository.save(item);
            }
            orderItemList.add(orderItem);
        }
    }

    private static Order createOrder(ConvertOrderDto convertOrderDto, Member member, List<OrderItem> orderItemList) {
        return Order.builder()
                .member(member)
                .orderItems(orderItemList)
                .orderStatus(convertOrderDto.getOrderStatus())
                .address(member.getAddress())
                .orderDate(convertOrderDto.getOrderDate())
                .build();
    }
}
