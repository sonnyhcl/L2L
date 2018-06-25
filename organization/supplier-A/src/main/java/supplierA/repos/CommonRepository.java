package supplierA.repos;

import lombok.Data;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Data
@Component
public class CommonRepository {
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());
    //TODO:the following properties should be configed in xxx.properties
    private final String orgId = "SA1001";
    private final String orgName = "深圳备件供应商";
    private final String host = "10.131.245.91";
    private final String port = "9021";
    private final String projectId = "supplier-A";
    private final String location = "深圳";

    private final String mscContextPath = "http://10.131.245.91:9042/msc";
    private final String slcContextPath = "http://10.131.245.91:9043/slc";

    @Autowired
    private RestTemplate restTemplate;
    public  CommonRepository(){
        //TODO:register organization in VMC m MSC
    }



}
