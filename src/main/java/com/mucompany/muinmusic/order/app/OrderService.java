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
import com.mucompany.muinmusic.item.domain.Item;
import com.mucompany.muinmusic.item.repository.ItemRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final PaymentService paymentService;
    private final CartRepository cartRepository;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

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

        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            int count = orderItem.getCount();
            Item item = itemRepository.findById(orderItem.getItemId()).orElseThrow();
            item.increase(count);
        }

        paymentService.paymentCancellation();
    }

    @Transactional
    public void partialCancel(Long orderId, Long memberId, List<Long> itemIdList) {
        Member loginMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);

        if (!order.getMember().equals(loginMember)) {
            throw new NotMatchTheOrdererException();
        }

        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            if (itemIdList.contains(orderItem.getItemId())) {
                // 부분 주문 취소
                orderItem.cancel();
                //재고 원복
                for (Long id : itemIdList) {
                    Item item = itemRepository.findById(id).orElseThrow();
                    item.increase(orderItem.getCount());
                }
            }
        }

        orderItems.forEach(orderItem -> {
            if (itemIdList.contains(orderItem.getItemId())) {
                orderItem.cancel();
                itemIdList.forEach(id-> {
                    Item item = itemRepository.findById(id).orElseThrow();
                    item.increase(orderItem.getCount());
                });
            }
        });

        paymentService.paymentCancellation();
    }

    @Transactional
    public void delete(Long orderId, Long memberId) {
        Member loginMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);

        validation(loginMember, order);

        order.softDelete();
    }

    @Transactional
    public List<OrderDto> getOrderHistory(Long memberId, Pageable pageable) {
        memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

         return orderRepository.findByMemberId(memberId,pageable).stream()
                .filter(order -> !order.isDelete())
                .map(OrderDto::new)
                .toList();
    }

    @Transactional
    public List<OrderDto> getCancelOrderHistory(Long memberId, Pageable pageable) {
        memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        return orderRepository.findByMemberId(memberId,pageable).stream()
                .filter(order -> !order.isDelete() && order.getOrderStatus().equals(OrderStatus.CANCELLED))
                .map(OrderDto::new)
                .toList();
    }

    private static void validation(Member loginMember, Order order) {
        if (!order.getMember().equals(loginMember)) {
            throw new NotMatchTheOrdererException();
        }

        if (order.getOrderStatus().equals(OrderStatus.SHIPPING)) {
            throw new UnableToDeleteOrderException();
        }
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
