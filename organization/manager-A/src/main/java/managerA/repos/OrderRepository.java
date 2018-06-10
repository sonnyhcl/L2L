package managerA.repos;


import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class OrderRepository {
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<Order> orders = new ArrayList<Order>();

    public void save(Order order){
        orders.add(order);
    }

}
