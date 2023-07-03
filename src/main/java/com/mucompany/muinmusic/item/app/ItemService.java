package com.mucompany.muinmusic.item.app;

import com.mucompany.muinmusic.cart.domain.CartItem;
import com.mucompany.muinmusic.exception.ItemNotFoundException;
import com.mucompany.muinmusic.item.domain.Item;
import com.mucompany.muinmusic.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void stockDecrease(CartItem cartItem) {
        Long itemId = cartItem.getItemId();
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        item.decrease(cartItem.getCount());
        log.info("item.getStock() = " + item.getStock());
    }
}
