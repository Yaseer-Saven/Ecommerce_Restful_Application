package com.example.demo.Controller;

import com.example.demo.Entity.Order;
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

        @PreAuthorize("hasRole('USER')")
        @PostMapping("/placeorder")
        public Order placeOrder(@RequestParam Long userId, @RequestParam int productId, @RequestParam int quantity, @RequestParam String address){
             return orderService.createOrder(userId, productId, quantity, address);
        }

        @PreAuthorize("hasRole('USER') OR hasRole('ADMIN')")
        @GetMapping("/getorders/{userId}")
        public List<Order> getOrdersByUserId(@PathVariable Long userId) {
                return orderService.getOrdersByUserId(userId);
        }

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/getorders")
        public List<Order> getOrders(){
                return orderService.getAllOrders();
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/updateOrderStatus")
        public ResponseEntity<Order> updateOrderStatus(@RequestParam int orderId){
                Order updateStatusOfOrder = orderService.updatingOrderStatus(orderId);
                if(updateStatusOfOrder != null) return ResponseEntity.ok(updateStatusOfOrder);
                else return ResponseEntity.notFound().build();
        }

        @PreAuthorize("hasRole('USER') OR hasRole('ADMIN')")
        @DeleteMapping("/deleteorder")
        public String cancelOrderByUserId(@RequestParam Long userId, @RequestParam int orderId){
                orderService.cancelOrderByUserId(userId,orderId);
                return "Order cancelled..!";
        }
}
