package com.example.demo.Service;

import com.example.demo.Entity.Product;
import com.example.demo.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Adding multiple products
    @Transactional
    public void saveAll(List<Product> product) {
        productRepository.saveAll(product);
    }

    // Adding single product
    @Transactional
    public void save(Product product) {
        productRepository.save(product);
    }

    // Retrieving all the products
    @Transactional
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Retrieving the product by product id
    @Transactional
    public Product getProductById(int productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id " + productId));
    }

    // Updating product details
    @Transactional
    public Product updateProduct(int productId, Product productDetails) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id " + productId));
        existingProduct.setProductName(productDetails.getProductName());
        existingProduct.setQuantity(productDetails.getQuantity());
        existingProduct.setPrice(productDetails.getPrice());
        return productRepository.save(existingProduct);
    }

    // Removing product by id
    @Transactional
    public void deleteProduct(int productId) {
        productRepository.deleteById(productId);
    }
}
