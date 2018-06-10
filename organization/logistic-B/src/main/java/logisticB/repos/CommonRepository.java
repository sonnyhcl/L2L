package logisticB.repos;

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
    private final String orgId = "logistic-A";
    private final String host = "10.131.245.91";
    private final String port = "9031";
    private final String projectId = "logistic-A";

    private final String slcContextPath = "http:10.131.245.91:9043/slc";
    private final String lvcContextPath = "http:10.131.245.91:9044/lvc";


    @Autowired
    private RestTemplate restTemplate;
    public CommonRepository(){
        //TODO:register organization in VMC m MSC
    }



}
