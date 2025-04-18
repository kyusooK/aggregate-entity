package aggregateentity.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Entity
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    protected OrderItem() {}

    protected OrderItem(String productName, Double price, Order order) {
        this.productName = productName;
        this.price = price;
        this.order = order;
    }

    public String getProductName() {
        return productName;
    }

    public Double getPrice() {
        return price;
    }
}
