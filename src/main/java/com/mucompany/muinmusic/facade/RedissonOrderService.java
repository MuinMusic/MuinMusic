package com.mucompany.muinmusic.facade;

import com.mucompany.muinmusic.exception.MemberNotFoundException;
import com.mucompany.muinmusic.exception.OrderFailException;
import com.mucompany.muinmusic.exception.OrderItemNotFoundException;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
import com.mucompany.muinmusic.order.app.OrderRequest;
import com.mucompany.muinmusic.order.app.OrderResponse;
import com.mucompany.muinmusic.order.app.OrderService;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.domain.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedissonOrderService {

    private final RedissonClient redissonClient;
    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;
    private final MemberRepository memberRepository;

    public OrderResponse placeOrder(OrderRequest orderRequest) {
        OrderResponse orderResponse = new OrderResponse();
        List<Long> orderItemIdList = orderRequest.getOrderItemIdList();
        List<OrderItem> orderItems = new ArrayList<>();

        Member member = memberRepository.findById(orderRequest.getMemberId()).orElseThrow(()-> new MemberNotFoundException());
        try {
            itemStockDecreaseWithLock(orderItemIdList, orderItems);
            orderResponse = orderService.save(orderItems, member, orderRequest);
        } catch (OrderFailException exception) {
            itemStockIncreaseWithLock(orderItemIdList);
        }
        return orderResponse;
    }

    private void itemStockDecreaseWithLock(List<Long> orderItemIdList, List<OrderItem> orderItems) {
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new OrderItemNotFoundException());
            Long itemId = orderItem.getItemId();

            RLock lock = redissonClient.getLock(itemId.toString());
            try {
                boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);
                if (!available) {
                    log.info("lock 획득 실패");
                }
                orderService.itemStockDecrease(orderItem, itemId);
                orderItems.add(orderItem);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }

    private void itemStockIncreaseWithLock(List<Long> orderItemIdList) {
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new OrderItemNotFoundException());
            Long itemId = orderItem.getItemId();

            RLock lock = redissonClient.getLock(itemId.toString());
            try {
                boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);
                if (!available) {
                    log.info("lock 획득 실패");
                }
                orderService.itemStockIncrease(orderItem, itemId);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }
}



