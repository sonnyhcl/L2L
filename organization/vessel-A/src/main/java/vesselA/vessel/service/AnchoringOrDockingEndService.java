package vesselA.vessel.service;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;

@Service("anchoringOrDockingEndService")
public class AnchoringOrDockingEndService implements ExecutionListener, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4885656684805353238L;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RuntimeService runtimeService;


    @Override
    public void notify(DelegateExecution exec) {
        // TODO Auto-generated method stub
        logger.info("Docking is over.");
        logger.info("********************AnchoringOrDockingEndService**********************");
        Map<String, Object> vars = exec.getVariables();
        boolean isMissing = (boolean)vars.get("isMissing");
        boolean isMeet = (boolean)vars.get("isMeet");
        logger.info("isMeet : "+isMeet+" isMissing : "+isMissing);
    }

}
