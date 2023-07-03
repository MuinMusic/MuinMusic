package com.mucompany.muinmusic.cart.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@NoArgsConstructor
@Getter
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "item_id")
    private Long itemId;

    @Column(nullable = false)
    private int count;

    @Column(nullable = false)
    private int totalAmount;

    public CartItem(Long itemId, int count, int totalAmount) {
        Assert.notNull(itemId, "itemId can not be null");
        Assert.notNull(count, "count can not be null");
        Assert.notNull(totalAmount, "totalAmount can not be null");

        this.itemId = itemId;
        this.count = count;
        this.totalAmount = totalAmount;
    }
}
