package logisticsA.eventGateway;

import org.activiti.engine.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class EventHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TaskService taskService;


}
