package iot.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import iot.domain.Path;
import iot.repos.CommonRepository;
import iot.repos.MapRepository;

import java.io.IOException;

//@RestController
public class ShadowController {
    private  static Logger logger = Logger.getLogger(ShadowController.class);
    @Value("${wagon.wid}")
    private String wid;

    @Autowired
    private CommonRepository commonRepository;
    @Autowired
    private MapRepository mapRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;


    @RequestMapping("/devices/shadow")
    ResponseEntity<Path> updateWagonDevice() throws IOException{
        return null;
    }
}
