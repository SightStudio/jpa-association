package persistence.sql.ddl;

import jakarta.persistence.*;

@Entity
@Table(name = "order_payments")
public class OrderPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardName;

    public OrderPayment() {}

    public Long getId() {
        return id;
    }
}
