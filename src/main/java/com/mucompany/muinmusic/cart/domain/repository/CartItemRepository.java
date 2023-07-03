package com.mucompany.muinmusic.cart.domain.repository;

import com.mucompany.muinmusic.cart.domain.CartItem;
import com.mucompany.muinmusic.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {

}
