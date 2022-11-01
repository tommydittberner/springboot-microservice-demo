package com.tommydittberner.orderservice.service;

import com.tommydittberner.orderservice.dto.InventoryResponse;
import com.tommydittberner.orderservice.dto.OrderLineItemsDto;
import com.tommydittberner.orderservice.dto.OrderRequest;
import com.tommydittberner.orderservice.model.Order;
import com.tommydittberner.orderservice.model.OrderLineItems;
import com.tommydittberner.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository repository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;

    public String placeOrder(OrderRequest orderRequest) {
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItemsList(orderLineItems)
                .build();

        List<String> skuCodes = order.getOrderLineItemsList()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        // provide our own spanId
        Span lookup = tracer.nextSpan().name("InventoryServiceLookup");

        try(Tracer.SpanInScope spanInScope = tracer.withSpan(lookup.start())) {
            InventoryResponse[] inventoryItems = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory", uriBuilder ->
                    uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();


            boolean allProductsInStock = inventoryItems != null
                && Arrays.stream(inventoryItems).allMatch(InventoryResponse::isInStock);

            if (allProductsInStock) {
                repository.save(order);
                return "Order placed successfully";
            }
            throw new IllegalArgumentException("Order could not be placed because the product is out of stock.");
        } finally {
            lookup.end();
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        return OrderLineItems.builder()
                .price(orderLineItemsDto.getPrice())
                .quantity(orderLineItemsDto.getQuantity())
                .skuCode(orderLineItemsDto.getSkuCode())
                .build();
    }


}
