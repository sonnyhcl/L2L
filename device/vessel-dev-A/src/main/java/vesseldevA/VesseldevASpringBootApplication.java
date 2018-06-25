package vesseldevA;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VesseldevASpringBootApplication {
    private Logger logger = LoggerFactory.getLogger(VesseldevASpringBootApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(VesseldevASpringBootApplication.class, args);
    }

}
