package com.mucompany.muinmusic.order.domain;

import com.mucompany.muinmusic.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@Getter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    @OneToMany
    private List<OrderItem> orderItems;

    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private String address;

    private LocalDateTime orderDate;

    @Builder
    public Order(Member member, List<OrderItem> orderItems, OrderStatus orderStatus, String address, LocalDateTime orderDate) {
        Assert.notNull(member, "member can not be null");
        Assert.notNull(address, "address can not be null");
        Assert.notNull(orderItems, "orderItems can not be null");
        Assert.notNull(orderStatus, "orderStatus can not be null");

        this.member = member;
        this.orderItems = orderItems;
        this.orderStatus = orderStatus;
        this.address = address;
        this.orderDate = orderDate;
    }

    public OrderStatus payed() {
       return this.orderStatus = OrderStatus.PAYMENT_COMPLETED;
    }

    public void shipping() {
         this.orderStatus = OrderStatus.SHIPPING;
    }
}
