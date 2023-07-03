package com.mucompany.muinmusic.cart.domain;

import com.mucompany.muinmusic.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Cart {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany
    private List<CartItem> cartItems;

    public Cart(Member member, List<CartItem> cartItems) {
        this.member = member;
        this.cartItems = cartItems;
    }
}
