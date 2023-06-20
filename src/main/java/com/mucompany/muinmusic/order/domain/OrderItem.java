package com.mucompany.muinmusic.order.domain;

import com.mucompany.muinmusic.Item.domain.Item;
import jakarta.persistence.*;
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

    @JoinColumn(name = "item_id")  // 외래 키를 생성하지 않고 값만 저장
    private Long itemId;

    @Column(nullable = false)
    private int count;

    @Column(nullable = false)
    private int totalAmount;

    public OrderItem(Long itemId, int count, int totalAmount) {
        Assert.notNull(itemId, "itemId can not be null");
        Assert.notNull(count, "count can not be null");
        Assert.notNull(totalAmount, "totalAmount can not be null");

        this.itemId = itemId;
        this.count = count;
        this.totalAmount = totalAmount;
    }
}
