package com.mucompany.muinmusic.order.domain.repository;

import com.mucompany.muinmusic.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findByOrderItemsId(Long orderItemId);

    List<Order> findByMemberId(Long memberId);
}
