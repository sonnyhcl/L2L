package managerA.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Data
@Service
public class CommonRepository {
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());
    //TODO:the following properties should be configed in xxx.properties
    private  String orgId;
    private  String host;
    private  String port;
    private  String projectId;
    private  String location;

    private  String vmcContextPath;
    private  String mscContextPath;
    public  CommonRepository(Environment environment){
        //TODO:register organization in VMC m MSC
        this.orgId = environment.getRequiredProperty("org.id");
        this.host = environment.getRequiredProperty("org.host");
        this.port = environment.getRequiredProperty("org.port");
        this.projectId = environment.getRequiredProperty("org.projectId");
        this.location = environment.getRequiredProperty("org.location");
        this.vmcContextPath = environment.getRequiredProperty("org.vmcContextPath");
        this.mscContextPath = environment.getRequiredProperty("org.mscContextPath");
    }

}
