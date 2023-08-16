package com.myproject.orderservice.service;

import com.myproject.orderservice.dto.InventryResponce;
import com.myproject.orderservice.dto.OrderLineItemsDto;
import com.myproject.orderservice.dto.OrederRequest;
import com.myproject.orderservice.event.OrderPlaceEvent;
import com.myproject.orderservice.model.Order;
import com.myproject.orderservice.model.OrderLineItems;
import com.myproject.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, OrderPlaceEvent> kafkaTemplate;

    public String placeOrder(OrederRequest orederRequest){
        Order order=new Order();
        order.setOrederNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItemsList =   orederRequest.getOrderLineItemsDtoList()
                            .stream()
                            .map(this::mapToDto)
                            .toList();
        order.setOrderLineItemsLise(orderLineItemsList);

        List<String> skuCodes =order.getOrderLineItemsLise().stream()
                        .map(OrderLineItems::getSkuCode)
                                .toList();

        InventryResponce[] inventryResponceArray = webClientBuilder.build().get()
                        .uri("http://inventory-service/api/inventory",
                                uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                        .retrieve()
                        .bodyToMono(InventryResponce[].class)
                        .block();

        boolean allProductInStock = Arrays.stream(inventryResponceArray).allMatch(InventryResponce::isInStock);

        if (allProductInStock){
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopic",new OrderPlaceEvent(order.getOrederNumber()));
            return "Order placed successfully";
        }else {
           throw  new IllegalArgumentException("Product Not in stock");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems=new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
