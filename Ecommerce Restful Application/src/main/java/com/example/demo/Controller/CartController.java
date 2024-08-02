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

    // End point to add the product in the cart
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/addToCart")
    public void addToCart(@RequestParam Long userId, @RequestParam int productId, @RequestParam int quantity) {
         try {
            cartService.addToCart(userId, productId, quantity);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   // End point to retrieve the cart details based on the user id
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/getCartDetails/{userId}")
    public ResponseEntity<List<CartDTO>> getCartDetails(@PathVariable Long userId) {
        try {
            return new ResponseEntity<>(cartService.getCartDetails(userId), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // End point to deletion of the cart based on user id
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/deleteCart/{userId}")
    public String deleteCart(@PathVariable Long userId) {
        try{
            cartService.deleteCart(userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(AccessDeniedException a){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
