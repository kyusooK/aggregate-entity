package aggregateentity.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private Integer qty;

    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    protected OrderItem() {}

    protected OrderItem(String productName, Integer qty, Double price, Order order) {
        this.productName = productName;
        this.qty = qty;
        this.price = price;
        this.order = order;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQty() {
        return qty;
    }

    public Double getPrice() {
        return price;
    }

    public void update(String productName, Integer qty, Double price) {
        this.productName = productName;
        this.qty = qty;
        this.price = price;
    }
}
