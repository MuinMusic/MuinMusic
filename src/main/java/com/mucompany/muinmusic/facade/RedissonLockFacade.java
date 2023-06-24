package com.mucompany.muinmusic.facade;

import com.mucompany.muinmusic.exception.OrderItemNotFoundException;
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

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedissonLockFacade {

    private final RedissonClient redissonClient;
    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;

    public OrderResponse placeOrder(OrderRequest orderRequest) {

        OrderResponse orderResponse = new OrderResponse();
        List<Long> orderItemIdList = orderRequest.getOrderItemIdList();
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new OrderItemNotFoundException());

            Long key = orderItem.getItemId();
            RLock lock = redissonClient.getLock(key.toString());

            try {
                boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);

                if (!available) {
                    System.out.println("lock 획득 실패");
                }
                orderResponse = orderService.placeOrder(orderRequest, orderItem, key);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
        return orderResponse;
    }
}
