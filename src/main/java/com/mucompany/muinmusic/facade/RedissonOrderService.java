package com.mucompany.muinmusic.facade;

import com.mucompany.muinmusic.cart.domain.Cart;
import com.mucompany.muinmusic.cart.domain.CartItem;
import com.mucompany.muinmusic.cart.domain.repository.CartItemRepository;
import com.mucompany.muinmusic.cart.domain.repository.CartRepository;
import com.mucompany.muinmusic.exception.CartNotFoundException;
import com.mucompany.muinmusic.exception.MemberNotFoundException;
import com.mucompany.muinmusic.item.app.ItemService;
import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.member.domain.repository.MemberRepository;
import com.mucompany.muinmusic.order.app.OrderRequest;
import com.mucompany.muinmusic.order.app.OrderResponse;
import com.mucompany.muinmusic.order.domain.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private final ItemService itemService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;


    @Transactional
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
            orderResponse = orderService.save(cartItemList, member, orderRequest);

        return orderResponse;
    }

    private void itemStockIncreaseWithLock(List<CartItem> cartItemList) {
        for (CartItem cartItem : cartItemList) {
            Long itemId = cartItem.getItemId();

            RLock lock = redissonClient.getLock(itemId.toString());
            try {
                boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);
                if (!available) {
                    log.info("lock 획득 실패");
                }
                orderService.itemStockIncrease(cartItem, itemId);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }
}



