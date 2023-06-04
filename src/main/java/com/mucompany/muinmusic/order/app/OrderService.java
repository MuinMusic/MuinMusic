package com.mucompany.muinmusic.order.app;

import com.mucompany.muinmusic.order.api.OrderRequestDto;
import com.mucompany.muinmusic.order.api.OrderResponseDto;

public interface OrderService {

    OrderResponseDto save(OrderRequestDto orderRequestDto);
}
