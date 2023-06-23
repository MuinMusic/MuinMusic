package com.mucompany.muinmusic.Item.domain;

import com.mucompany.muinmusic.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    public Item(String name, int price, int stock) {

        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public void decrease(int count) {
        if (this.stock < count) {
            throw new OutOfStockException();
        }
        this.stock = this.stock - count;
    }
}
