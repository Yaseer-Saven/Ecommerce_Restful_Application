package com.example.demo.Entity;

import com.example.demo.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "product_id")
    private int productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "price")
    private double price;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "order_date")
    private LocalDate orderDate ;

    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    public Order(User user, int productId, String productName, double price, LocalDate orderDate, int count, String address, OrderStatus status) {
        this.user = user;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.orderDate = orderDate;
        this.quantity = count;
        this.address = address;
        this.status = status;
    }


}
