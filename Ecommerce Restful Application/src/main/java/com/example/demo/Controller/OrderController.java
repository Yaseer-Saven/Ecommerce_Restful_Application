package com.example.demo.Controller;

import com.example.demo.DTO.OrderDTO;
import com.example.demo.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    // End point for placing the order
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/placeorder")
    public ResponseEntity<List<OrderDTO>> placeOrder(@RequestParam int cartId, @RequestParam String address) {
        try {
            List<OrderDTO> orderDTOs = orderService.createOrder(cartId, address);
            return ResponseEntity.ok(orderDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    // End point to retrieve order based on the id
    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN')")
    @GetMapping("/getorders/{userId}")
    public List<OrderDTO> getOrdersByUserId(@PathVariable Long userId) {
        return orderService.getOrdersByUserId(userId);
    }

    // End point to retrieve all orders
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getorders")
    public List<OrderDTO> getOrders() {
        return orderService.getAllOrders();
    }

    // End point to update the order status
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateOrderStatus")
    public ResponseEntity<OrderDTO> updateOrderStatus(@RequestParam int orderId) {
        OrderDTO updateStatusOfOrder = orderService.updatingOrderStatus(orderId);
        if (updateStatusOfOrder != null) return ResponseEntity.ok(updateStatusOfOrder);
        else return ResponseEntity.notFound().build();
    }

    // End point to cancel the order
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/deleteorder")
    public String cancelOrderByUserId(@RequestParam int orderId) {
        orderService.cancelOrder(orderId);
        return "Order cancelled..!";
    }
}
