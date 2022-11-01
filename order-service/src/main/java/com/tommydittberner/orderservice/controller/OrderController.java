package com.tommydittberner.orderservice.controller;

import com.tommydittberner.orderservice.dto.OrderRequest;
import com.tommydittberner.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(
        name = "inventory",
        fallbackMethod = "placeOrderFallback"
    )
    @TimeLimiter(name = "inventory") // has to be the same as in properties
    @Retry(name = "inventory")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        // has to be completable future because of possible timeout
        return CompletableFuture.supplyAsync(() -> orderService.placeOrder(orderRequest));
    }

    private CompletableFuture<String> placeOrderFallback(OrderRequest orderRequest, RuntimeException ex) {
        return CompletableFuture.supplyAsync(() -> "Something went wrong, please try again shortly!");
    }
}
