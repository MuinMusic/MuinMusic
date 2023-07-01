package com.mucompany.muinmusic.order.api;

import com.mucompany.muinmusic.facade.RedissonOrderService;
import com.mucompany.muinmusic.order.app.OrderRequest;
import com.mucompany.muinmusic.order.app.OrderResponse;
import com.mucompany.muinmusic.order.app.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final RedissonOrderService redissonOrderService;

    @PostMapping(value = "/orders")
    public ResponseEntity<OrderResponseDto> create(@RequestBody @Valid OrderRequestDto orderRequestDto) {

        OrderRequest orderRequest = createOrderRequest(orderRequestDto);

        OrderResponse orderResponse = redissonOrderService.placeOrder(orderRequest);

        OrderResponseDto orderResponseDto = new OrderResponseDto(orderResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDto);
    }

    @PostMapping(value = "/orders/{orderId}/cancel")
    public ResponseEntity<Object> cancel(@PathVariable Long orderId, @RequestParam Long memberId) {
        orderService.cancel(orderId, memberId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private static OrderRequest createOrderRequest(OrderRequestDto orderRequestDto) {
        return OrderRequest.builder()
                .memberId(orderRequestDto.getMemberId())
                .orderItemIdList(orderRequestDto.getOrderItemIdList())
                .address(orderRequestDto.getAddress())
                .orderDate(LocalDateTime.now())
                .build();
    }
}
