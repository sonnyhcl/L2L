package vesseldevice;


import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class VesselSpringBootApplication {
    private static Logger logger = Logger.getLogger(VesselSpringBootApplication.class);
    public static void main(String[] args) throws AWSIotException {
        SpringApplication.run(VesselSpringBootApplication.class, args);
    }

}
