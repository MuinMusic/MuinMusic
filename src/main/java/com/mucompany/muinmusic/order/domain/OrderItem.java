package com.mucompany.muinmusic.order.domain;

import com.mucompany.muinmusic.cart.domain.CartItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    public OrderItem(CartItem cartItem) {
        Assert.notNull(cartItem, "cartItem can not be null");

        this.itemId = cartItem.getItemId();
        this.count = cartItem.getCount();
        this.totalAmount = cartItem.getTotalAmount();
    }
}
