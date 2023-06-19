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
public class OrderServiceImpl implements OrderService {

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

        //주문 아이템 존재여부 체크
        List<Long> orderItemIdList = orderRequest.getOrderItemIdList();
        List<OrderItem> orderItemList = new ArrayList<>();

        //상품이름,가격,재고수량 체크
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

    @Transactional
    public void validate(List<Long> orderItemIdList, List<OrderItem> orderItemList) {
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new OrderItemNotFoundException());

            //todo 컨트롤러 테스트중 pessimisticLock 걸었더니 itemNotFound 예외가 안터져서 따로 추가
            itemRepository.findById(orderItem.getItem().getId()).orElseThrow(() -> new ItemNotFoundException());
            Item item = itemRepository.findByIdWithPessimisticLock(orderItem.getItem().getId());

            // 주문상품명과 ,가격 결제전에 변경되었는지 확인하기
            itemNameAndPriceCheck(orderItem, item);

            //수량 체크 및 변경 된 수량 업데이트
            item.decrease(orderItem);
            System.out.println("item.getStock() = " + item.getStock());

            itemRepository.save(item);

            orderItemList.add(orderItem);
        }
    }

    private static void itemNameAndPriceCheck(OrderItem orderItem, Item item) {
        if (!orderItem.getItem().getName().equals(item.getName())) {
            throw new ItemNameNotMatchException();
        }
        if (orderItem.getItem().getPrice() != item.getPrice()) {
            throw new ItemPriceNotMatchException();
        }
    }

    private static Order createOrder(OrderRequest orderRequest, Member member, List<OrderItem> orderItemList) {
        return Order.builder()
                .member(member)
                .orderItems(orderItemList)
                .address(member.getAddress())
                .orderStatus(OrderStatus.ORDERED)
                .orderDate(orderRequest.getOrderDate())
                .build();
    }
}
