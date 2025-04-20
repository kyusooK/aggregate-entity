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
                item.setProductName(productName);
                item.setPrice(price);
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
    
        // 새 Order 객체 생성
        Order order = new Order();
        
        // placeOrderCommand에서 값을 가져와 설정
        order.setUserId(placeOrderCommand.getUserId());
        order.setQty(placeOrderCommand.getQty());
        order.setInventoryId(placeOrderCommand.getInventoryId());
        order.setOrderStatus(placeOrderCommand.getOrderStatus());
        
        if (placeOrderCommand.getOrderItems() != null) {
            for (OrderItem item : placeOrderCommand.getOrderItems()) {
                // OrderItem의 추가를 위해 Order.java의 addOrderItem() 호출
                order.addOrderItem(item.getProductName(), item.getPrice());
            }
        }
        repository().save(order);
    
        OrderPlaced orderPlaced = new OrderPlaced(order);
        orderPlaced.publishAfterCommit();
    }
//>>> Clean Arch / Port Method
//<<< Clean Arch / Port Method
    public void modifyOrder(ModifyOrderCommand modifyOrderCommand){
    
        repository().findById(this.getId()).ifPresent(order -> {
            // ModifyOrderCommand에서 전달받은 orderItems가 null이 아닌 경우에만 처리
            if (modifyOrderCommand.getOrderItems() != null) {
                // 각 OrderItem에 대해 수정 작업 수행
                for (OrderItem item : modifyOrderCommand.getOrderItems()) {
                    // 기존 updateOrderItem 메서드를 사용하여 각 항목 수정
                    order.updateOrderItem(item.getId(), item.getProductName(), item.getPrice());
                }
            }
            
            repository().save(order);
        });
        OrderModified orderModified = new OrderModified(order);
        orderModified.publishAfterCommit();
    } 
    //>>> Clean Arch / Port Method
}
//>>> DDD / Aggregate Root
