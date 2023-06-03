package com.mucompany.muinmusic.orders.app;

import com.mucompany.muinmusic.Item.domain.Item;
import com.mucompany.muinmusic.Item.repository.ItemRepository;
import com.mucompany.muinmusic.exception.ItemNotFoundException;
import com.mucompany.muinmusic.exception.MemberNotFoundException;
import com.mucompany.muinmusic.exception.OutOfStockException;
import com.mucompany.muinmusic.member.repository.MemberRepository;
import com.mucompany.muinmusic.orderItem.domain.OrderItem;
import com.mucompany.muinmusic.orders.domain.Orders;
import com.mucompany.muinmusic.orders.api.AddOrderRequestDto;
import com.mucompany.muinmusic.orders.api.OrderResponseDto;
import com.mucompany.muinmusic.orders.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository ordersRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;


    @Override
    public OrderResponseDto save(AddOrderRequestDto addOrderRequestDto) {
        Orders orders = new Orders(addOrderRequestDto);

        //회원 유효한지 체크
        memberRepository.findById(orders.getMember().getId()).orElseThrow(MemberNotFoundException::new);

        //주문 아이템 존재여부 체크
        List<OrderItem> orderItems = orders.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            Item item = itemRepository.findById(orderItem.getItem().getId()).orElseThrow(ItemNotFoundException::new);

            //재고 수량 파악
            if (item.getStock() < orderItem.getCount()) {
                throw new OutOfStockException();
            }
        }

        Orders saveOrders = ordersRepository.save(orders);
        return new OrderResponseDto(saveOrders);
    }
}
