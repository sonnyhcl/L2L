package vesselA.repos;

import lombok.Data;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
/**
 * vessel data cache for eventGateway shadow and related process data by vId
 * @author bqzhu
 */
@Data
@Service
public class CommonRepository {
    //TODO:the following properties should be configed in xxx.properties
    private  String orgId;
    private  String host;
    private  String port;
    private  String projectId;

    private  String vmcContextPath;
    private  String lvcContextPath;
    private  String vdevContextPath;

    private int zoomInVal = 1000;
    private int defaultDelayHour = 6;

    public CommonRepository(Environment environment){
        //TODO:register organization in VMC and LVC
        orgId = environment.getRequiredProperty("org.id");
        host = environment.getRequiredProperty("org.host");
        port = environment.getRequiredProperty("org.port");
        projectId = environment.getRequiredProperty("org.projectId");
        vmcContextPath = environment.getRequiredProperty("org.vmcContextPath");
        lvcContextPath = environment.getRequiredProperty("org.lvcContextPath");
        vdevContextPath = environment.getRequiredProperty("org.vdevContextPath");
    }

}
