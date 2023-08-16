package com.myproject.orderservice.dto;

import com.myproject.orderservice.model.OrderLineItems;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrederRequest {
    private List<OrderLineItemsDto> orderLineItemsDtoList;
}
