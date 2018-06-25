package vesselA.repos;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vesselA.domain.VesselShadow;

import java.util.Map;

/**
 * vessel data cache for awsiot shadow and related process data by vId
 * @author bqzhu
 */
@Component("vesselCache")
@Data
public class CommonRepository {
    //船的Iot数据, vId-->vesselShadow
    private  Map<String , VesselShadow> vesselShadows;

    //TODO:the following properties should be configed in xxx.properties
    private final String orgId = "VA1001";
    private final String host = "10.131.245.91";
    private final String port = "9001";
    private final String projectId = "vessel-A";

    private final String vmcContextPath = "http://10.131.245.91:9041/vmc";
    private final String lvcContextPath = "http://10.131.245.91:9044/lvc";
    private final String vdevContextPath = "http://10.131.245.91:9051/vessel-dev-A";


    @Autowired
    private RestTemplate restTemplate;
    public CommonRepository(){
        //TODO:register organization in VMC and LVC
    }

}
