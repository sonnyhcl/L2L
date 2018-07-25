package iot;


import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class WagonBSpringBootApplication {
    private static Logger logger = Logger.getLogger(WagonBSpringBootApplication.class);


    public static void main(String[] args){
        SpringApplication.run(WagonBSpringBootApplication.class, args);
    }

}
