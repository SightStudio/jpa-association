package persistence.sql.ddl;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    public List<OrderItem> orderItems;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    public List<OrderPayment> orderPayments;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;

    public Order() {}

    public Order(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public Long getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void addOrderItem(OrderItem orderItem) {
        if (this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        this.orderItems.add(orderItem);
    }

    public List<OrderPayment> getOrderPayments() {
        return orderPayments;
    }
}
