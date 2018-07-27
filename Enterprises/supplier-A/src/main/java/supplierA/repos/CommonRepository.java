package supplierA.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Data
@Service
public class CommonRepository {
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());
    //TODO:the following properties should be configed in xxx.properties
    private  String orgId;
    private  String orgName;
    private  String host;
    private  String port;
    private  String projectId;
    private  String location;

    private  String mscContextPath;
    private  String slcContextPath;

    @Autowired
    private RestTemplate restTemplate;
    public  CommonRepository(Environment environment){
        //TODO:register organization in VMC m MSC
        orgId = environment.getRequiredProperty("org.id");
        orgName = environment.getRequiredProperty("org.orgName");
        host = environment.getRequiredProperty("org.host");
        port = environment.getRequiredProperty("org.port");
        projectId = environment.getRequiredProperty("org.projectId");
        location = environment.getRequiredProperty("org.location");
        mscContextPath = environment.getRequiredProperty("org.mscContextPath");
        slcContextPath = environment.getRequiredProperty("org.slcContextPath");
        logger.debug(this.toString());
    }



}
