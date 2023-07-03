package com.mucompany.muinmusic.cart.domain.repository;

import com.mucompany.muinmusic.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Long> {

}
