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
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied to access this resource");
        } catch (NoSuchElementException n) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found for id: " + cartId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    // End point to retrieve order based on the id
    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN')")
    @GetMapping("/getorders/{userId}")
    public List<OrderDTO> getOrdersByUserId(@PathVariable Long userId) {
         try {
            return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
        } catch (AccessDeniedException a) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed to access this resource");
        } catch (NoSuchElementException n) {
            return ResponseEntity.status(404).body("No orders found for : " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    // End point to retrieve all orders
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getorders")
    public List<OrderDTO> getOrders() {
          try {
            return ResponseEntity.ok(orderService.getAllOrders());
        } catch (AccessDeniedException m) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to access this resource");
        } catch (NoSuchElementException n) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No order found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
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
        try {
            orderService.cancelOrder(orderId);
            return new ResponseEntity<>("Order cancelled..!", HttpStatus.OK);
        } catch (NoSuchElementException n) {
            return new ResponseEntity<>("No order exist for id: " + orderId, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>("Not allowed to access this resource", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("Order not found for id: " + orderId, HttpStatus.NOT_FOUND);
        }
    }
}
