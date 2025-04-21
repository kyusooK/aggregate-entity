package aggregateentity.domain;

import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import aggregateentity.OrderApplication;
import lombok.Data;

@Entity
@Table(name="Order_table")
@Data
//<<< DDD / Aggregate Root
public class Order  {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;    
        
    private String userId;   
    
    private Date orderDate;
    
    
    @Embedded
    private InventoryId inventoryId;    
        
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;    
        
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new java.util.ArrayList<>();
    
    public void addOrderItem(String productName, Integer qty, Double price) {
        OrderItem orderItem = new OrderItem(productName, qty, price, this);
        orderItems.add(orderItem);
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    public void updateOrderItem(Long id, String productName, Integer qty, Double price) {
        for (OrderItem item : orderItems) {
            if (item.getId().equals(id)) {
                item.update(productName, qty, price);
                break;
            }
        }
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
    }
    

    //<<< Clean Arch / Port Method
    public void placeOrder(PlaceOrderCommand placeOrderCommand){
    // 현재 Order 객체에 직접 값을 설정
        this.setUserId(placeOrderCommand.getUserId());
        this.setOrderDate(new Date());
        this.setInventoryId(placeOrderCommand.getInventoryId());
        this.setOrderStatus(placeOrderCommand.getOrderStatus());
        
        if (placeOrderCommand.getOrderItems() != null) {
            for (OrderItem item : placeOrderCommand.getOrderItems()) {
                this.addOrderItem(item.getProductName(), item.getPrice());
            }
        }
        
        repository().save(this);

        OrderPlaced orderPlaced = new OrderPlaced(this);
        orderPlaced.publishAfterCommit();
    } 
//>>> Clean Arch / Port Method
//<<< Clean Arch / Port Method
    public void modifyOrder(ModifyOrderCommand modifyOrderCommand){
    
        repository().findById(this.getId()).ifPresent(order -> {
            // ModifyOrderCommand에서 전달받은 orderItems가 null이 아닌 경우에만 처리
            if (modifyOrderCommand.getOrderItems() != null) {
                // 기존 orderItems를 모두 제거
                order.orderItems.clear();
                
                // 새로운 orderItems 추가
                for (OrderItem item : modifyOrderCommand.getOrderItems()) {
                    order.addOrderItem(item.getProductName(), item.getPrice());
                }
            }
            repository().save(order);

            OrderModified orderModified = new OrderModified(this);
            orderModified.publishAfterCommit();
        });
        
    }
}
//>>> DDD / Aggregate Root
