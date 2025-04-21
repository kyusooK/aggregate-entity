package aggregateentity.domain;

import aggregateentity.domain.*;
import aggregateentity.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class OrderPlaced extends AbstractEvent {

    private Long id;
    private String userId;
    private Object inventoryId;
    private Object orderStatus;
    private Object orderItems;
    private Date orderDate;
}
