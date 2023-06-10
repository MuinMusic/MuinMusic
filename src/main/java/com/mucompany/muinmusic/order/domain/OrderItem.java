package com.mucompany.muinmusic.order.domain;

import com.mucompany.muinmusic.Item.domain.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.springframework.util.Assert;

@Entity
@NoArgsConstructor
@Getter
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Item item;

    @Column(nullable = false)
    private int count;

    @Column(nullable = false)
    private int totalAmount;

    public OrderItem(Item item, int count, int totalAmount) {
        Assert.notNull(item,"item can not be null");
        Assert.notNull(count,"count can not be null");
        Assert.notNull(totalAmount,"totalAmount can not be null");

        this.item = item;
        this.count = count;
        this.totalAmount = totalAmount;
    }
}
