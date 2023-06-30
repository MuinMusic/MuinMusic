package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.item.domain.Item;
import com.mucompany.muinmusic.item.repository.ItemRepository;
import com.mucompany.muinmusic.exception.ItemNotFoundException;
import com.mucompany.muinmusic.exception.MemberNotFoundException;
import com.mucompany.muinmusic.exception.NotMatchTheOrdererException;
import com.mucompany.muinmusic.exception.OrderFailException;
import com.mucompany.muinmusic.exception.OrderNotFoundException;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
import com.mucompany.muinmusic.order.domain.Order;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.domain.OrderStatus;
import com.mucompany.muinmusic.order.domain.repository.OrderRepository;
import com.mucompany.muinmusic.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Primary
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final PaymentService paymentService;


    @Override
    @Transactional
    public void itemStockDecrease(OrderItem orderItem, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException());
        item.decrease(orderItem.getCount());
        log.info("item.getStock() = " + item.getStock());
        itemRepository.save(item);
    }

    @Override
    @Transactional
    public OrderResponse save(List<OrderItem> orderItemList, Member member, OrderRequest orderRequest) {
        Order order = createOrder(member, orderItemList);
        try {
            orderRepository.save(order);
        } catch (Exception e) {
            throw new OrderFailException();
        }
        //결제 결과를 확인하고 주문상태를 변경한다. ORDER -> PAYMENT_COMPLETED
        OrderStatus orderStatus = order.getOrderStatus();
        if (paymentService.completePayment()) {
            orderStatus = order.payed();
        }
        return new OrderResponse(orderRequest, orderStatus);
    }

    @Override
    @Transactional
    public void cancel(Long orderId, Long memberId) {
        Member loginMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException());
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException());
        //주문자와 로그인 회원 일치하는지 체크
        if (!order.getMember().equals(loginMember)) {
            throw new NotMatchTheOrdererException();
        }
        //취소 검증
        order.cancel();
    }

    @Override
    @Transactional
    public void itemStockIncrease(OrderItem orderItem, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        item.increase(orderItem.getCount());
        itemRepository.save(item);
        log.info("stock" + item.getStock());
    }


    public Order createOrder(Member member, List<OrderItem> orderItemList) {
        return Order.builder()
                .member(member)
                .orderItems(orderItemList)
                .address(member.getAddress())
                .orderStatus(OrderStatus.ORDERED)
                .orderDate(LocalDateTime.now())
                .build();
    }
}
