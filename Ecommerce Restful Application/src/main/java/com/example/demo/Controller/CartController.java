package com.example.demo.Controller;

import com.example.demo.DTO.CartDTO;
import com.example.demo.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/addToCart")
    public void addToCart(@RequestParam Long userId, @RequestParam int productId, @RequestParam int quantity) {
        cartService.addToCart(userId, productId, quantity);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/getCartDetails/{userId}")
    public List<CartDTO> getCartDetails(@PathVariable Long userId) {
        return cartService.getCartDetails(userId);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/deleteCart/{userId}")
    public String deleteCart(@PathVariable Long userId) {
        return cartService.deleteCart(userId);
    }
}
