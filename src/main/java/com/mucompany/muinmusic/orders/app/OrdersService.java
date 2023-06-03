package com.mucompany.muinmusic.orders.app;

import com.mucompany.muinmusic.orders.api.AddOrderRequestDto;
import com.mucompany.muinmusic.orders.api.OrderResponseDto;

public interface OrdersService {

    OrderResponseDto save(AddOrderRequestDto addOrderRequestDto);
}
