package com.mucompany.muinmusic.order.domain;

import com.mucompany.muinmusic.cart.domain.CartItem;
import com.mucompany.muinmusic.exception.OrderCancellationException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@NoArgsConstructor
@Getter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "item_id")
    private Long itemId;

    @Column(nullable = false)
    private int count;

    @Column(nullable = false)
    private int totalAmount;

    @Column
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public OrderItem(CartItem cartItem) {
        Assert.notNull(cartItem, "cartItem can not be null");

        this.itemId = cartItem.getItemId();
        this.count = cartItem.getCount();
        this.totalAmount = cartItem.getTotalAmount();
        this.orderStatus = OrderStatus.PAYMENT_COMPLETED;
    }

    public void cancel() {
        if (orderStatus.equals(OrderStatus.SHIPPING)) {
            throw new OrderCancellationException();
        }
        this.orderStatus = OrderStatus.CANCELLED;
    }
}
