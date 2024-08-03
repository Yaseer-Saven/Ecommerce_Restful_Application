package com.example.demo.Service;

import com.example.demo.customclasses.AuthenticatingCurrentUser;
import com.example.demo.DTO.OrderDTO;
import com.example.demo.Entity.Cart;
import com.example.demo.Entity.Order;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.User;
import com.example.demo.customclasses.OrderStatus;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticatingCurrentUser authenticatingCurrentUser;

    // Placing the order by passing the user id, product id, quantity, address ( make this works as if we pass cart id and address then the order should be placed )
    @Transactional
    public List<OrderDTO> createOrder(int cartId, String address) {
        User currentUser = authenticatingCurrentUser.getCurrentUser();
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found with id " + cartId));
        // Check if the cart belongs to the current user or not
        if (!cart.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }

        // Create an order from the cart
        Order order = new Order();
        order.setUser(currentUser);
        order.setProductId(cart.getProduct().getProductId());
        order.setProductName(cart.getProduct().getProductName());
        order.setPrice(cart.getProduct().getPrice());
        order.setQuantity(cart.getQuantity());
        order.setOrderDate(LocalDate.now());
        order.setAddress(address);
        order.setStatus(OrderStatus.ORDERED);
        orderRepository.save(order);

        // Removing the product from the cart after placing the order
        cartRepository.delete(cart);
        OrderDTO orderDTO = new OrderDTO(order);
        List<OrderDTO> orderDTOs = new ArrayList<>();
        orderDTOs.add(orderDTO);
        return orderDTOs;
    }

    // Retrieving the all orders ( Accessible for only ADMIN )
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(OrderDTO::new).collect(Collectors.toList());
    }

    // Retrieving the orders by passing the user id
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        User currentUser = authenticatingCurrentUser.getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(OrderDTO::new).collect(Collectors.toList());
    }

    // Updating the order status as order is delivered ( Order status is updated to DELIVERED )
    @Transactional
    public OrderDTO updatingOrderStatus(int orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order updateOrder = order.get();
            updateOrder.setStatus(OrderStatus.DELIVERED);
            Order savedOrder = orderRepository.save(updateOrder);
            return new OrderDTO(savedOrder);
        }
        throw new IllegalArgumentException("Order not found with id: " + orderId);
    }

    // Cancelling the order (  Order status is updated to CANCELLED )
    @Transactional
    public Order cancelOrder(int orderId) {
        User currentUser = authenticatingCurrentUser.getCurrentUser();

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order existingOrder = optionalOrder.get();
            if (!existingOrder.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("You are not allowed to cancel this order");
            }
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
            throw new IllegalArgumentException("Order not found with orderId=" + orderId);
        }
    }

}
