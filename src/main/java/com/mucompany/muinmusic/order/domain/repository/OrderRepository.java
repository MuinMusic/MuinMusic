package com.mucompany.muinmusic.order.domain.repository;

import com.mucompany.muinmusic.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findByOrderItemsId(Long cartItemId);
}
