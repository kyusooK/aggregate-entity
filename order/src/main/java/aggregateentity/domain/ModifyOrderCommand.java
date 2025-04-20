package aggregateentity.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class ModifyOrderCommand {

    private List<OrderItem> orderItems;
}
