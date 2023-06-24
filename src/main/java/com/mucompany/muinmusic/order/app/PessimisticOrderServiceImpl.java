package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.Item.domain.Item;
import com.mucompany.muinmusic.Item.repository.ItemRepository;
import com.mucompany.muinmusic.exception.*;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PessimisticOrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        //회원 유효한지 체크
        Member member = memberRepository.findById(orderRequest.getMemberId()).orElseThrow(() -> new MemberNotFoundException());

        List<Long> orderItemIdList = orderRequest.getOrderItemIdList();
        List<OrderItem> orderItemList = new ArrayList<>();

        decrease(orderItemIdList);

        validate(orderItemIdList, orderItemList);

        Order order = createOrder(orderRequest, member, orderItemList);
        orderRepository.save(order);

        //결제 결과를 확인하고 주문상태를 변경한다. ORDER -> PAYMENT_COMPLETED
        OrderStatus orderStatus = order.getOrderStatus();
        if (paymentService.completePayment()) {
            orderStatus = order.payed();
        }
        return new OrderResponse(orderRequest, orderStatus);
    }

    private void validate(List<Long> orderItemIdList, List<OrderItem> orderItemList) {
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow();
            Item item = itemRepository.findById(orderItem.getItemId()).orElseThrow(() -> new ItemNotFoundException());

            // 주문상품명과 ,가격 결제전에 변경되었는지 확인하기
            itemNameAndPriceCheck(orderItem, item);

            orderItemList.add(orderItem);
        }
    }

    public void decrease(List<Long> orderItemIdList) {
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new OrderItemNotFoundException());
            Item item = itemRepository.findByIdWithPessimisticLock(orderItem.getItemId()).orElseThrow(() -> new ItemNotFoundException());

            //수량 체크 및 변경 된 수량 업데이트
            item.decrease(orderItem.getCount());
            System.out.println("item.getStock() = " + item.getStock());

            itemRepository.save(item);
        }
    }

    private void itemNameAndPriceCheck(OrderItem orderItem, Item item) {
        Long itemId = orderItem.getItemId();
        Item findItem = itemRepository.findById(itemId).orElseThrow();

        if (!findItem.getName().equals(item.getName())) {
            throw new ItemNameNotMatchException();
        }
        if (findItem.getPrice() != item.getPrice()) {
            throw new ItemPriceNotMatchException();
        }
    }

    public Order createOrder(OrderRequest orderRequest, Member member, List<OrderItem> orderItemList) {
        return Order.builder()
                .member(member)
                .orderItems(orderItemList)
                .address(member.getAddress())
                .orderStatus(OrderStatus.ORDERED)
                .orderDate(orderRequest.getOrderDate())
                .build();
    }
    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest, OrderItem orderItem, Long key) {
        return null;
    }

    @Override
    public void cancel(Long orderId, Long memberId) {

    }
}
