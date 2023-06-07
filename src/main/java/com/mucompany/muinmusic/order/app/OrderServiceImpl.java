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
import com.mucompany.muinmusic.order.domain.OrderStatus;
import com.mucompany.muinmusic.order.domain.repository.OrderItemRepository;
import com.mucompany.muinmusic.order.domain.repository.OrderRepository;
import com.mucompany.muinmusic.payment.PaymentService;
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
    private final PaymentService paymentService;

    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        //회원 유효한지 체크
        Member member = memberRepository.findById(orderRequest.getMemberId()).orElseThrow(MemberNotFoundException::new);

        //주문 아이템 존재여부 체크
        List<Long> orderItemIdList = orderRequest.getOrderItemIdList();
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemCheck(orderItemIdList, orderItemList);

        Order order = createOrder(orderRequest, member, orderItemList);
        orderRepository.save(order);

        //결제 결과를 확인하고 주문상태를 변경한다. ORDER -> PAYMENT_COMPLETED
        OrderStatus orderStatus = order.getOrderStatus();
        if (paymentService.paymentResult("success")) {
            order.changeOrderStatus(OrderStatus.PAYMENT_COMPLETED);
            orderStatus = order.getOrderStatus();
        }
        return new OrderResponse(orderRequest, orderStatus);
    }

    private void orderItemCheck(List<Long> orderItemIdList, List<OrderItem> orderItemList) {
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(OrderItemNotFoundException::new);

            Item item = itemRepository.findById(orderItem.getItem().getId()).orElseThrow(ItemNotFoundException::new);

            //수량 체크 및 변경 된 수량 업데이트
            if (item.getStock() < orderItem.getCount()) {
                throw new OutOfStockException();
            } else {
                item.decreaseStock(orderItem.getCount());
                itemRepository.save(item);
            }
            orderItemList.add(orderItem);
        }
    }

    private static Order createOrder(OrderRequest orderRequest, Member member, List<OrderItem> orderItemList) {
        return Order.builder()
                .member(member)
                .orderItems(orderItemList)
                .address(member.getAddress())
                .orderStatus(OrderStatus.ORDER)
                .orderDate(orderRequest.getOrderDate())
                .build();
    }
}
