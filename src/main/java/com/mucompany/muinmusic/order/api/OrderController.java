package com.mucompany.muinmusic.order.api;

import com.mucompany.muinmusic.order.app.OrderRequest;
import com.mucompany.muinmusic.order.app.OrderResponse;
import com.mucompany.muinmusic.order.app.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/orders")
    public ResponseEntity<OrderResponseDto> placeOrder(@RequestBody @Valid OrderRequestDto orderRequestDto) {

        OrderRequest orderRequest = createOrderRequest(orderRequestDto);

        OrderResponse orderResponse = orderService.placeOrder(orderRequest);

        OrderResponseDto orderResponseDto = OrderResponseDto.builder()
                .memberId(orderResponse.memberId())
                .orderItemIdList(orderResponse.orderItemIdList())
                .orderStatus(orderResponse.orderStatus())
                .address(orderRequestDto.address())
                .orderDate(orderResponse.orderDate())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDto);
    }

    @PostMapping(value = "/orders/{orderId}/cancel")
    public ResponseEntity<Object> cancel(@PathVariable Long orderId, @RequestParam Long memberId) {
        orderService.cancel(orderId, memberId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(value = "/orders/{orderId}")
    public ResponseEntity<Object> softDelete(@PathVariable Long orderId, @RequestParam Long memberId) {
        orderService.delete(orderId, memberId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/orders")
    public ResponseEntity<List<OrderDto>> getOrderHistory(@RequestParam Long memberId,
                                                          @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                                                          Pageable pageable) {

        List<OrderDto> orderHistory = orderService.getOrderHistory(memberId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(orderHistory);
    }

    @GetMapping(value = "/orders/cancel")
    public ResponseEntity<List<OrderDto>> getCancelOrderHistory(@RequestParam Long memberId,
                                                                @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                                                                Pageable pageable) {
        List<OrderDto> orderHistory = orderService.getCancelOrderHistory(memberId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(orderHistory);
    }

    @PostMapping(value = "/orders/{orderId}/partial_cancel")
    public ResponseEntity<Object> partialCancel(@PathVariable Long orderId,
                                                @RequestParam Long memberId,
                                                @RequestParam List<Long> itemIdList) {
        orderService.partialCancel(orderId, memberId, itemIdList);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private static OrderRequest createOrderRequest(OrderRequestDto orderRequestDto) {
        return OrderRequest.builder()
                .memberId(orderRequestDto.memberId())
                .cartId(orderRequestDto.cartId())
                .address(orderRequestDto.address())
                .orderDate(LocalDateTime.now())
                .build();
    }
}
