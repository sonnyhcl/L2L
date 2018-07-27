package managerA.coordinator;

import org.activiti.app.service.api.UserCache;
import org.activiti.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象Controller，提供一些基础的方法、属性
 */
@RestController
public class AbstractController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected ProcessEngine processEngine;
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected HistoryService historyService;
    @Autowired
    protected IdentityService identityService;
    @Autowired
    protected ManagementService managementService;
    @Autowired
    protected FormService formService;
    @Autowired
    protected UserCache userCache;
}
