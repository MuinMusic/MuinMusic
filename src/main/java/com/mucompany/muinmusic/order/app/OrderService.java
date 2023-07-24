package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.cart.domain.Cart;
import com.mucompany.muinmusic.cart.domain.CartItem;
import com.mucompany.muinmusic.cart.domain.repository.CartRepository;
import com.mucompany.muinmusic.exception.CartNotFoundException;
import com.mucompany.muinmusic.exception.MemberNotFoundException;
import com.mucompany.muinmusic.exception.NotMatchTheOrdererException;
import com.mucompany.muinmusic.exception.OrderNotFoundException;
import com.mucompany.muinmusic.exception.UnableToDeleteOrderException;
import com.mucompany.muinmusic.item.app.ItemService;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.repository.MemberRepository;
import com.mucompany.muinmusic.order.api.OrderDto;
import com.mucompany.muinmusic.order.domain.Order;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.domain.OrderStatus;
import com.mucompany.muinmusic.order.domain.repository.OrderRepository;
import com.mucompany.muinmusic.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final PaymentService paymentService;
    private final CartRepository cartRepository;
    private final ItemService itemService;

    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        Member member = memberRepository.findById(orderRequest.getMemberId()).orElseThrow(MemberNotFoundException::new);
        Cart cart = cartRepository.findById(orderRequest.getCartId()).orElseThrow(CartNotFoundException::new);

        List<CartItem> cartItems = cart.getCartItems();
        cartItems.forEach(cartItem -> itemService.stockDecrease(cartItem.getItemId(), cartItem.getCount()));

        Order order = createOrder(member, cartItems);
        orderRepository.save(order);

        if (paymentService.completePayment()) {
            order.payed();
        }

        List<Long> orderItemIdList = order.getOrderItems().stream()
                .map(OrderItem::getId)
                .toList();

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
        Member loginMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);

        if (!order.getMember().equals(loginMember)) {
            throw new NotMatchTheOrdererException();
        }

        order.cancel();
        paymentService.paymentCancellation();
    }

    @Transactional
    public void softDelete(Long orderId, Long memberId) {
        Member loginMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);

        Optional.of(order.getMember())
                .filter(member -> member.equals(loginMember))
                .orElseThrow(NotMatchTheOrdererException::new);

        Optional.of(order.getOrderStatus())
                .filter(orderStatus -> orderStatus.equals(OrderStatus.SHIPPING))
                .ifPresent(o -> {
                    throw new UnableToDeleteOrderException();
                });

        order.softDelete();
    }

    public List<OrderDto> getOrderHistory(Long memberId, Pageable pageable) {
        memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

         return orderRepository.findByMemberId(memberId,pageable).stream()
                .filter(order -> !order.isDelete())
                .map(OrderDto::new)
                .toList();
    }

    private Order createOrder(Member member, List<CartItem> cartItems) {

        List<OrderItem> orderItemList = cartItems.stream()
                .map(OrderItem::new)
                .toList();

        return Order.builder()
                .member(member)
                .orderItems(orderItemList)
                .address(member.getAddress())
                .orderStatus(OrderStatus.ORDERED)
                .orderDate(LocalDateTime.now())
                .build();
    }
}
