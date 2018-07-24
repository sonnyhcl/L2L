package logisticsA.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Data
@Service
public class CommonRepository {
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());
    //TODO:the following properties should be configed in xxx.properties
    private  String orgId;
    private  String host;
    private  String port;
    private  String projectId;

    private  String slcContextPath;
    private  String lvcContextPath;

    @Autowired
    private RestTemplate restTemplate;
    public CommonRepository(Environment environment){
        //TODO:register organization in VMC m MSC
        orgId = environment.getRequiredProperty("org.id");
        host = environment.getRequiredProperty("org.host");
        port = environment.getRequiredProperty("org.port");
        projectId = environment.getRequiredProperty("org.projectId");
        slcContextPath = environment.getRequiredProperty("org.slcContextPath");
        lvcContextPath = environment.getRequiredProperty("org.lvcContextPath");
    }



}
