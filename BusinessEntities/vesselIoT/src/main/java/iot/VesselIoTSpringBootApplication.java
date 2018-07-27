package iot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VesselIoTSpringBootApplication {
    private Logger logger = LoggerFactory.getLogger(VesselIoTSpringBootApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(VesselIoTSpringBootApplication.class, args);
    }

}
