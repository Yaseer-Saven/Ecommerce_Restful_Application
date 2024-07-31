package com.example.demo.DTO;

import com.example.demo.Entity.Cart;
import lombok.*;
@ToString
@AllArgsConstructor
@Data
public class CartDTO {

    private int cartId;
    private Long userId;
    private String username;
    private int productId;
    private int quantity;
    private String productName;
    private double productPrice;

    public CartDTO(Cart cart) {
        this.cartId = cart.getCartId();
        this.userId = cart.getUser().getId();
        this.username = cart.getUser().getUsername();
        this.productId = cart.getProduct().getProductId();
        this.quantity = cart.getProduct().getQuantity();
        this.productName = cart.getProduct().getProductName();
        this.productPrice = cart.getProduct().getPrice();
    }
}
