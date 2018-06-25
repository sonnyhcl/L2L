package managerA.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Data
@Component
public class CommonRepository {
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());
    //TODO:the following properties should be configed in xxx.properties
    private final String orgId = "MA1001";
    private final String host = "10.131.245.91";
    private final String port = "9011";
    private final String projectId = "manager-A";

    private final String vmcContextPath = "http://10.131.245.91:9041/vmc";
    private final String mscContextPath = "http://10.131.245.91:9042/msc";
    public  CommonRepository(){
        //TODO:register organization in VMC m MSC
    }

}
