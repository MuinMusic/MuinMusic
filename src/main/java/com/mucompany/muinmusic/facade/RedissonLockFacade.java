package com.mucompany.muinmusic.facade;

import com.mucompany.muinmusic.Item.domain.Item;
import com.mucompany.muinmusic.Item.repository.ItemRepository;
import com.mucompany.muinmusic.exception.ItemNotFoundException;
import com.mucompany.muinmusic.exception.OrderItemNotFoundException;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.domain.repository.OrderItemRepository;
import com.mucompany.muinmusic.order.domain.repository.OrderRepository;
import com.mucompany.muinmusic.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonLockFacade {

    private final RedissonClient redissonClient;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;

    public void decreaseRedissonLock(List<Long> orderItemIdList) {
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new OrderItemNotFoundException());
            int count = orderItem.getCount();

            Item item = itemRepository.findById(orderItem.getItemId()).orElseThrow(() -> new ItemNotFoundException());
            Long id = item.getId();
            RLock lock = redissonClient.getLock(id.toString());
            try {
                boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);

                if (!available) {
                    System.out.println("lock 획득 실패");
                    return;
                }

                itemDecrease(id, count);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }

    @Transactional
    public void itemDecrease(Long id, int count) {
        Item item = itemRepository.findById(id).orElseThrow();
        item.decrease(count);
        System.out.println("item.getStock() = " + item.getStock());
        itemRepository.save(item);
    }
}
