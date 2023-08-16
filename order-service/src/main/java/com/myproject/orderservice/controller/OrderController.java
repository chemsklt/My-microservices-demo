package com.myproject.orderservice.controller;

import com.myproject.orderservice.dto.OrederRequest;
import com.myproject.orderservice.service.OrderService;
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

    private  final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<String> placeOrder(@RequestBody OrederRequest orederRequest){
        return CompletableFuture.supplyAsync(() ->orderService.placeOrder(orederRequest));

    }

    public CompletableFuture<String>  fallbackMethod(OrederRequest orederRequest,RuntimeException runtimeException){
        return CompletableFuture.supplyAsync(() -> "Ops! something went wrong try again later");

    }
}
