package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.Item.domain.Item;
import com.mucompany.muinmusic.Item.repository.ItemRepository;
import com.mucompany.muinmusic.order.domain.OrderItem;
import com.mucompany.muinmusic.order.domain.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private ItemRepository itemRepository;
    private OrderItemRepository orderItemRepository;

    public StockService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional
    public void decrease(Long id, OrderItem orderItem) {
        Item item = itemRepository.findByIdWithPessimisticLock(id);
        item.decrease(orderItem);
        System.out.println("item.getStock() = " + item.getStock());

        itemRepository.save(item);
    }

    @Transactional
    public void decrease2(Long id, OrderItem orderItem) {
        Item item = itemRepository.findById(id).orElseThrow();
        item.decrease(orderItem);
        System.out.println("item.getStock() = " + item.getStock());

        itemRepository.save(item);
    }
}
