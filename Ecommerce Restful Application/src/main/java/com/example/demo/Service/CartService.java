package com.example.demo.Service;
import com.example.demo.DTO.CartDTO;
import com.example.demo.Entity.Cart;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.User;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void addToCart(Long userId, int productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        // Checking whether product is in the cart or not
        Cart existingCartItem = cartRepository.findByUserAndProduct(user, product).get();
        if (existingCartItem != null) {
            // Update quantity if product already in cart
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            cartRepository.save(existingCartItem);
        } else {
            Cart cartItem = new Cart(user, product, quantity);
            cartRepository.save(cartItem);
        }
    }

    public List<CartDTO> getCartDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        List<Cart> carts = cartRepository.findByUser(user);
        List<CartDTO> cartDTOs = new ArrayList<>();
        for (Cart cart : carts) {
            CartDTO cartDTO = new CartDTO(cart);
            cartDTOs.add(cartDTO);
        }
        return cartDTOs;
    }

    @Transactional
    public String deleteCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        cartRepository.deleteByUser(user);
        return "Deleted all items from the cart for user with id: " + userId;
    }
}
