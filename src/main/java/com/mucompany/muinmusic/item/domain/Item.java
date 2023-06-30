package com.mucompany.muinmusic.item.domain;

import com.mucompany.muinmusic.exception.InsufficientStockException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
            throw new InsufficientStockException();
        }
        this.stock = this.stock - count;
    }

    public void increase(int count) {
        this.stock += count;
    }
}
