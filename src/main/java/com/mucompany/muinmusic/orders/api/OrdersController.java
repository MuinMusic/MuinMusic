package com.mucompany.muinmusic.orders.api;

import com.mucompany.muinmusic.orders.app.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;

    public ResponseEntity<OrderResponseDto> create(@RequestBody AddOrderRequestDto addOrderRequestDto) {

        OrderResponseDto orderResponseDto = ordersService.save(addOrderRequestDto);

        return ResponseEntity.ok(orderResponseDto);
    }
}
