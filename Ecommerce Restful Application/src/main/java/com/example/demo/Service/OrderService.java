package com.example.demo.Service;

import com.example.demo.Entity.Cart;
import com.example.demo.Entity.Order;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.User;
import com.example.demo.OrderStatus;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductService productService;

    @Transactional
    public Order createOrder(Long userId, int productId, int quantity, String address) {
        User user = customUserDetailsService.getUserById(userId);
        Product product = productService.getProductById(productId);
        if (user == null) throw new IllegalArgumentException("User not found with id: " + userId);
        if (product == null) throw new IllegalArgumentException("Product not found with id: " + productId);

        int availableQuantity = product.getQuantity();
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero");
        if (quantity > availableQuantity) throw new IllegalArgumentException("Not enough quantity available for product: " + product.getProductName());

        String productName = product.getProductName();
        double price = product.getPrice();
        LocalDate orderDate = LocalDate.now();
        OrderStatus status = OrderStatus.ORDERED;

        Order newOrder = new Order(user, productId, productName, price, orderDate, quantity, address, status);
        Order savedOrder = orderRepository.save(newOrder);

        // Update product quantity
        product.setQuantity(availableQuantity - quantity);
        productService.save(product);

        // Remove the product from the cart
        Optional<Cart> optionalCartItemToRemove = cartRepository.findByUserAndProduct(user, product);
        if (optionalCartItemToRemove.isPresent()) {
            Cart cartItemToRemove = optionalCartItemToRemove.get();
            cartRepository.delete(cartItemToRemove);
        } else {
            throw new IllegalStateException("Cart item not found for user " + user.getId() + " and product " + product.getProductId());
        }

        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public Order updatingOrderStatus(int orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order updateOrder = order.get();
            updateOrder.setStatus(OrderStatus.DELIVERED);
            return orderRepository.save(updateOrder);
        }
        throw new IllegalArgumentException("Order not found with id: " + orderId);
    }

    @Transactional
    public Order cancelOrderByUserId(Long userId, int orderId) {
        User user = customUserDetailsService.getUserById(userId);
        if (user != null) {
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (optionalOrder.isPresent()) {
                Order existingOrder = optionalOrder.get();
                if (existingOrder.getStatus() == OrderStatus.ORDERED) {
                    existingOrder.setStatus(OrderStatus.CANCELLED);
                    orderRepository.save(existingOrder);

                    Product product = productService.getProductById(existingOrder.getProductId());
                    int currentQuantity = product.getQuantity();
                    product.setQuantity(currentQuantity + existingOrder.getQuantity());
                    productService.save(product);

                    return existingOrder;
                } else {
                    throw new IllegalArgumentException("Order with orderId=" + orderId + " cannot be cancelled.");
                }
            } else {
                throw new IllegalArgumentException("Order not found for userId=" + userId + " and orderId=" + orderId);
            }
        }
        throw new IllegalArgumentException("User not found with id: " + userId);
    }
}
