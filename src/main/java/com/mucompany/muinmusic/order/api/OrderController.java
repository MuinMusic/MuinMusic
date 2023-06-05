package com.mucompany.muinmusic.order.api;

import com.mucompany.muinmusic.order.app.ConvertOrderDto;
import com.mucompany.muinmusic.order.app.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/orders")
    public ResponseEntity<OrderResponseDto> create(@RequestBody OrderRequestDto orderRequestDto) {

        ConvertOrderDto convertOrderDto = ConvertOrderDto.builder()
                .memberId(orderRequestDto.getMemberId())
                .orderItemIdList(orderRequestDto.getOrderItemIdList())
                .orderStatus(orderRequestDto.getOrderStatus())
                .address(orderRequestDto.getAddress())
                .orderDate(orderRequestDto.getOrderDate())
                .build();

        ConvertOrderDto result = orderService.save(convertOrderDto);

        OrderResponseDto orderResponseDto = new OrderResponseDto(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDto);
    }
}
