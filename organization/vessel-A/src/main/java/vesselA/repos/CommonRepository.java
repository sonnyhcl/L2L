package vesselA.repos;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vesselA.domain.VesselShadow;

import java.util.Map;

/**
 * vessel data cache for awsiot shadow and related process data by vId
 * @author bqzhu
 */
@Data
@Service
public class CommonRepository {
    //船的Iot数据, vId-->vesselShadow
    private  Map<String , VesselShadow> vesselShadows;

    @Autowired
    private Environment environment;

    //TODO:the following properties should be configed in xxx.properties
    private  String orgId = "VA1001";
    private  String host = "10.131.245.91";
    private  String port = "9001";
    private  String projectId = "vessel-A";

    private  String vmcContextPath = "http://10.131.245.91:9041/vmc";
    private  String lvcContextPath = "http://10.131.245.91:9044/lvc";
    private  String vdevContextPath = "http://10.131.245.91:9051/vessel-dev-A";


    @Autowired
    private RestTemplate restTemplate;
    public CommonRepository(){
        //TODO:register organization in VMC and LVC
//        orgId = environment.getRequiredProperty("org.id");
//        host = environment.getRequiredProperty("org.host");
//        port = environment.getRequiredProperty("org.port");
//        projectId = environment.getRequiredProperty("org.projectId");
//        vmcContextPath = environment.getRequiredProperty("org.vmcContextPath");
//        lvcContextPath = environment.getRequiredProperty("org.lvcContextPath");
//        vdevContextPath = environment.getRequiredProperty("org.vdevContextPath");
    }

}
