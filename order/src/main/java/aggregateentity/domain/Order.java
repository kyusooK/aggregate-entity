package aggregateentity.domain;

import aggregateentity.OrderApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;
import java.time.LocalDate;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;


@Entity
@Table(name="Order_table")
@Data

//<<< DDD / Aggregate Root
public class Order  {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;    
        
    private String userId;    
        
    private Integer qty;    
        
    @Embedded
    private InventoryId inventoryId;    
        
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;    
        
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new java.util.ArrayList<>();
    
    public void addOrderItem(String productName, Double price) {
        OrderItem orderItem = new OrderItem(productName, price, this);
        orderItems.add(orderItem);
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    public void updateOrderItem(Long id, String productName, Double price) {
        for (OrderItem item : orderItems) {
            if (item.getId().equals(id)) {
                item.update(productName, price);
                break;
            }
        }
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
    }


    public static OrderRepository repository(){
        OrderRepository orderRepository = OrderApplication.applicationContext.getBean(OrderRepository.class);
        return orderRepository;
    }



    //<<< Clean Arch / Port Method
    public void placeOrder(PlaceOrderCommand placeOrderCommand){
        
        //implement business logic here:
        


        OrderPlaced orderPlaced = new OrderPlaced(this);
        orderPlaced.publishAfterCommit();
    }
    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public void cancelOrder(CancelOrderCommand cancelOrderCommand){
        
        //implement business logic here:


        OrderCanceled orderCanceled = new OrderCanceled(this);
        orderCanceled.publishAfterCommit();
    }
    //>>> Clean Arch / Port Method
}
//>>> DDD / Aggregate Root
