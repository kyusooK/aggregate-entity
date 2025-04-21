package aggregateentity.domain;

import aggregateentity.domain.*;
import aggregateentity.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class OrderModified extends AbstractEvent {

    private Long id;
    private String userId;
    private List<OrderItem> orderItems;
    private InventoryId inventoryId;
    private OrderStatus orderStatus;
    private Date orderDate;

    public OrderModified(Order aggregate) {
        super(aggregate);
    }

    public OrderModified() {
        super();
    }
}
//>>> DDD / Domain Event
