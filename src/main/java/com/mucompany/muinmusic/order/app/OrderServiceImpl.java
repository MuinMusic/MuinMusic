package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.cart.domain.Cart;
import com.mucompany.muinmusic.cart.domain.CartItem;
import com.mucompany.muinmusic.cart.domain.repository.CartRepository;
import com.mucompany.muinmusic.exception.*;
import com.mucompany.muinmusic.item.app.ItemService;
import com.mucompany.muinmusic.item.domain.Item;
import com.mucompany.muinmusic.item.repository.ItemRepository;
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

@Primary
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final PaymentService paymentService;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ItemService itemService;

    @Transactional
    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        OrderResponse orderResponse = new OrderResponse();
        Long cartId = orderRequest.getCartId();
        List<CartItem> cartItemList = new ArrayList<>();

        //회원이 존재하는지 확인
        Member member = memberRepository.findById(orderRequest.getMemberId()).orElseThrow(() -> new MemberNotFoundException());

        //재고 감소 (재고감소 로직을 뒤로 빼면 동시성 적용 안되던 이유를 찾아라)
        //주문하려는 아이템 이름,가격 변경되었는지 체크 -> 결제

        //장바구니에서 가져온
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new CartNotFoundException());
        List<CartItem> cartItems = cart.getCartItems();

        for (CartItem cartItem : cartItems) {
            //재고차감
            Long itemId = cartItem.getItemId();
            itemService.itemStockDecrease(cartItem, itemId);
            //아이템 서비스를 따로 만들자
            cartItemList.add(cartItem);
        }
        //주문이 들어간다
        orderResponse = save(cartItemList, member, orderRequest);

        return orderResponse;
    }


    @Override
    @Transactional
    public void itemStockDecrease(CartItem cartItem, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException());
        item.decrease(cartItem.getCount());
        log.info("item.getStock() = " + item.getStock());
        itemRepository.save(item);
    }

    @Override
    @Transactional
    public OrderResponse save(List<CartItem> cartItemList, Member member, OrderRequest orderRequest) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartItem cartItem : cartItemList) {
            OrderItem orderItem = new OrderItem(cartItem);
            orderItemRepository.save(orderItem);
            orderItemList.add(orderItem);
        }

        Order order = createOrder(member, orderItemList);
        try {
            orderRepository.save(order);
        } catch (Exception e) {
            throw new OrderFailException();
        }
        OrderStatus orderStatus = order.getOrderStatus();
        List<OrderItem> orderItems = order.getOrderItems();
        List<Long> orderItemIdList = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            orderItemIdList.add(orderItem.getId());
        }

        if (paymentService.completePayment()) {
            orderStatus = order.payed();
        }
        return new OrderResponse(orderRequest, orderItemIdList, orderStatus);
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
    public void itemStockIncrease(com.mucompany.muinmusic.cart.domain.CartItem cartItem, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        item.increase(cartItem.getCount());
        itemRepository.save(item);
        log.info("stock" + item.getStock());
    }


    public Order createOrder(Member member, List<OrderItem> orderItems) {
        return Order.builder()
                .member(member)
                .orderItems(orderItems)
                .address(member.getAddress())
                .orderStatus(OrderStatus.ORDERED)
                .orderDate(LocalDateTime.now())
                .build();
    }
}
