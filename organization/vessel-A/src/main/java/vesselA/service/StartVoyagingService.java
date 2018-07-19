package vesselA.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vesselA.controller.AwsClient;
import vesselA.domain.VesselShadow;
import vesselA.repos.ShadowRepository;

import java.io.Serializable;
import java.util.Map;


@Service("startVoyagingService")
public class StartVoyagingService implements ExecutionListener, Serializable {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final long serialVersionUID = 4149621500319226872L;

    @Autowired
    private AwsClient awsClient;
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ShadowRepository shadowRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void notify(DelegateExecution execution) {
        // TODO Auto-generated method stub
        //notify the  vessel device of start or continuing reported data
        logger.info("--startVoyagingService--");
        String pid = execution.getProcessInstanceId();
        Map<String, Object> vars = execution.getVariables();
        String vid = vars.get("vid").toString();
        //call voyaging-service for vessel device.
        VesselShadow vs = shadowRepository.findById(vid);
        vs.setStatus("Voyaging");
        runtimeService.setVariable(pid , "status" , vs.getStatus());
        ObjectNode payloadObjectNode = objectMapper.createObjectNode();
        //从JsonFactory创建一个JsonGenerator生成器的实例
        if ("Voyaging".equals(vs.getStatus())) {
            logger.info("next.");
            //TODO: notify vessel device of start next voyaging
            awsClient.notifyVoyaging("NOT_FIRST" , vid);
        }
    }

}
