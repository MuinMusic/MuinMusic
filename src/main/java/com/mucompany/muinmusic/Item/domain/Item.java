package com.mucompany.muinmusic.Item.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
@Entity
@NoArgsConstructor
@Getter
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public void decreaseStock(int count) {
        this.stock -= count;
    }
}
