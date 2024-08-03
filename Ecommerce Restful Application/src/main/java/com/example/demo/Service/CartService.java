package com.example.demo.Service;
import com.example.demo.customclasses.AuthenticatingCurrentUser;
import com.example.demo.DTO.CartDTO;
import com.example.demo.Entity.Cart;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.User;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticatingCurrentUser authenticatingCurrentUser;

    // Adding the product in cart
    @Transactional
    public void addToCart(Long userId, int productId, int quantity) {
        User currentUser =  authenticatingCurrentUser.getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        System.out.println("User and Product fetched successfully");

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        // Checking available quantity of the product
        int availableQuantity = product.getQuantity();
        if (availableQuantity < quantity) {
            throw new IllegalArgumentException("Not enough quantity available for product: " + product.getProductName());
        }

        // Checking whether product is in the cart or not
        Optional<Cart> existingCartItemOptional = cartRepository.findByUserAndProduct(currentUser, product);
        if (existingCartItemOptional.isPresent()) {
            // If the product is in the cart then updating the quantity of the product in the cart
            Cart existingCartItem = existingCartItemOptional.get();
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            cartRepository.save(existingCartItem);
            System.out.println("Product quantity updated in the cart");
        } else {
            System.out.println("Adding product to the cart");
            // If the product is not present in the cart, adding the product to the cart
            Cart cartItem = new Cart(currentUser, product, quantity);
            cartRepository.save(cartItem);
            System.out.println("Product added to the cart");
        }

        // Updating the product quantity after adding the product to the cart
        product.setQuantity(availableQuantity - quantity);
        productRepository.save(product);
        System.out.println("Product quantity updated in the inventory");
    }

    // Retrieving the cart details by passing the user id
    @Transactional
    public List<CartDTO> getCartDetails(Long userId) {
        User currentUser =  authenticatingCurrentUser.getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }
        List<Cart> carts = cartRepository.findByUser(currentUser);
        return carts.stream().map(CartDTO::new).collect(Collectors.toList());
    }

    // Deleting the cart by passing the user id
    @Transactional
    public String deleteCart(Long userId) {
        User currentUser =  authenticatingCurrentUser.getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }
        // Retrieve all cart items for the user
        List<Cart> cartItems = cartRepository.findByUser(currentUser);
        if (cartItems.isEmpty()) {
            return "No items in the cart for user with id: " + userId;
        }
        cartItems.stream().forEach(cartItem -> {
            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity() + cartItem.getQuantity());
            productRepository.save(product);
        });
        // Delete all cart items for the user
        cartRepository.deleteByUser(currentUser);
        return "Deleted cart for user with id: " + userId;
    }
}
