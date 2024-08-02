package com.example.demo.Repository;

import com.example.demo.Entity.Cart;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByUser(User user);
    void deleteByUser(User user);
    Optional<Cart> findByUserAndProduct(User user, Product product);
    void delete(Cart cartItemToRemove);
}
