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
            if (modifyOrderCommand.getOrderItems() != null) {
                // 새로운 항목 목록
                List<OrderItem> newItems = modifyOrderCommand.getOrderItems();
                // 기존 항목을 복사 (순회 중 컬렉션을 수정하는 문제를 방지)
                List<OrderItem> existingItems = new java.util.ArrayList<>(order.orderItems);
                // 기존 항목 처리
                for (OrderItem existingItem : existingItems) {
                    boolean found = false;
                    for (OrderItem newItem : newItems) {
                        if (newItem.getId() != null && newItem.getId().equals(existingItem.getId())) {
                            // 기존 항목 업데이트
                            order.updateOrderItem(existingItem.getId(), 
                                            newItem.getProductName(), 
                                            newItem.getQty(), 
                                            newItem.getPrice());
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        // 새 목록에 없는 항목은 제거
                        order.removeOrderItem(existingItem);
                    }
                }
                // 새로운 항목 추가
                for (OrderItem newItem : newItems) {
                    if (newItem.getId() == null) {
                        order.addOrderItem(newItem.getProductName(), 
                                        newItem.getQty(), 
                                        newItem.getPrice());
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
