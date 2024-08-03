package com.example.demo.DTO;

import com.example.demo.Entity.Order;
import com.example.demo.customclasses.OrderStatus;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private int productId;
    private String productName;
    private double price;
    private int quantity;
    private LocalDate orderDate;
    private String address;
    private OrderStatus status;

    public OrderDTO(Order order) {
        this.orderId = order.getOrderId();
        this.userId = order.getUser().getId();
        this.productId = order.getProductId();
        this.productName = order.getProductName();
        this.price = order.getPrice();
        this.quantity = order.getQuantity();
        this.orderDate = order.getOrderDate();
        this.address = order.getAddress();
        this.status = order.getStatus();
    }
}
