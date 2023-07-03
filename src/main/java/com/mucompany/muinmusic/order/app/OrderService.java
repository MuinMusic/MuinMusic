package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.cart.domain.Cart;
import com.mucompany.muinmusic.cart.domain.CartItem;
import com.mucompany.muinmusic.cart.domain.repository.CartRepository;
import com.mucompany.muinmusic.exception.*;
import com.mucompany.muinmusic.item.app.ItemService;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
import com.mucompany.muinmusic.order.domain.Order;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.domain.OrderStatus;
import com.mucompany.muinmusic.order.domain.repository.OrderItemRepository;
import com.mucompany.muinmusic.order.domain.repository.OrderRepository;
import com.mucompany.muinmusic.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final PaymentService paymentService;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ItemService itemService;

    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        //회원이 존재하는지 확인
        Member member = memberRepository.findById(orderRequest.getMemberId()).orElseThrow(MemberNotFoundException::new);

        //장바구니에서 가져온
        Cart cart = cartRepository.findById(orderRequest.getCartId()).orElseThrow(CartNotFoundException::new);

        List<CartItem> cartItems = cart.getCartItems();

        for (CartItem cartItem : cartItems) {
            //재고차감
            itemService.stockDecrease(cartItem);
        }
        //주문이 들어간다
        return save(cartItems, member);
    }

    private OrderResponse save(List<CartItem> cartItemList, Member member) {
        List<OrderItem> orderItemList = cartItemList.stream()
                .map(OrderItem::new)
                .collect(Collectors.toList());

        Order order = createOrder(member, orderItemList);

        orderRepository.save(order);

        if (paymentService.completePayment()) {
            order.payed();
        }

        List<Long> orderItemIdList = orderItemList.stream()
                .map(OrderItem::getId)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .memberId(order.getMember().getId())
                .address(order.getAddress())
                .orderStatus(order.getOrderStatus())
                .orderItemIdList(orderItemIdList)
                .orderDate(order.getOrderDate())
                .build();
    }

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


    private Order createOrder(Member member, List<OrderItem> orderItems) {
        return Order.builder()
                .member(member)
                .orderItems(orderItems)
                .address(member.getAddress())
                .orderStatus(OrderStatus.ORDERED)
                .orderDate(LocalDateTime.now())
                .build();
    }
}
