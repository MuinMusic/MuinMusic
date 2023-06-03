package com.mucompany.muinmusic.orders.domain;

import com.mucompany.muinmusic.member.domain.Member;
import com.mucompany.muinmusic.orderItem.domain.OrderItem;
import com.mucompany.muinmusic.orders.api.AddOrderRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Orders {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    @OneToMany
    private List<OrderItem> orderItems;

    @Column(nullable = false)
    private String orderStatus;

    @Column(nullable = false)
    private String address;

    private LocalDateTime orderDate;

    @Builder
    public Orders(AddOrderRequestDto addOrderRequestDto) {
        Assert.notNull(addOrderRequestDto.getMember(),"member can not be null");
        Assert.notNull(addOrderRequestDto.getAddress(),"address can not be null");
        Assert.notNull(addOrderRequestDto.getOrderItems(),"orderItems can not be null");

        this.member = addOrderRequestDto.getMember();
        this.orderItems = addOrderRequestDto.getOrderItems();
        this.orderStatus = addOrderRequestDto.getOrderStatus();
        this.address = addOrderRequestDto.getAddress();
        this.orderDate = addOrderRequestDto.getOrderDate();
    }
}
