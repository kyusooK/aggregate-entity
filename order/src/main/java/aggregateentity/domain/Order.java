package aggregateentity.domain;

import java.util.Collections;
import java.util.List;

import aggregateentity.OrderApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;
import java.time.LocalDate;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public static OrderRepository repository(){
        OrderRepository orderRepository = OrderApplication.applicationContext.getBean(OrderRepository.class);
        return orderRepository;
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
                this.addOrderItem(item.getProductName(), item.getQty(), item.getPrice());
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
                // 수정된 OrderItem만 업데이트 (ID가 있는 항목만 처리)
                for (OrderItem newItem : modifyOrderCommand.getOrderItems()) {
                    if (newItem.getId() != null) {
                        // 기존 항목 중 ID가 있으면 업데이트
                        order.updateOrderItem(newItem.getId(), newItem.getProductName(), newItem.getQty(), newItem.getPrice());
                    }
                }
            }
            repository().save(order);

            OrderModified orderModified = new OrderModified(this);
            orderModified.publishAfterCommit();
        });
    }
}
//>>> DDD / Aggregate Root
