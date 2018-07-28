package iot.restapi;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import iot.domain.Path;
import iot.repos.CommonRepository;
import iot.repos.MapRepository;
import iot.service.pubSub.MessagePublisher;
import iot.service.shadow.WagonDevice;
import iot.util.PathUtil;

import java.io.IOException;
import java.util.HashMap;

//@RestController
public class APIController {
    private  static Logger logger = Logger.getLogger(APIController.class);

    @Value("${wagon.wid}")
    private String wid;


    @Value("${wagon.topic.updateShadow}")
    private String updateTopic;


    @Autowired
    private CommonRepository commonRepository;
    @Autowired
    private MapRepository mapRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WagonDevice wagonDevice;
    @RequestMapping("/home")
    String home() {
        logger.info("test rest api.");
        logger.debug("wagon id = "+wid+" -- "+commonRepository.getWid());
        return "bazhu Hello **** World!";
    }

    @RequestMapping("/path")
    ResponseEntity<Path> testPath() throws IOException{
//        String url = "http://restapi.amap.com/v4/direction/truck?width=2.5&strategy=5&size=2&weight=10&axis=2&origin=100.340417,27.376994&destination=118.800095,32.146214&output=json&key=ec15fc50687bd2782d7e45de6d08a023";
      //  String pathInfo = restTemplate.getForObject(url , String.class);
        String pathInfo =mapRepository.PlanPath("100.340417", "27.376994"
                , "118.800095" , "32.146214");
        JsonNode pathNode = objectMapper.readTree(pathInfo);
        Path path = PathUtil.extractPath(pathNode);
        logger.debug(path.toString());
        return new ResponseEntity<Path>(path,HttpStatus.OK);
    }

    @RequestMapping(value = "/shadow" , method = RequestMethod.POST)
    ResponseEntity<String> updateShadow(@RequestBody HashMap<String , Object> shadow) throws JsonProcessingException {
        String id = shadow.get("id").toString();
        double longitude = Double.parseDouble(shadow.get("longitude").toString());
        double latitude = Double.parseDouble(shadow.get("latitude").toString());
        double speed = Double.parseDouble(shadow.get("speed").toString());
        double movedDistance = Double.parseDouble(shadow.get("movedDistance").toString());
        wagonDevice.updateLongitude(longitude);
        wagonDevice.updateLatitude(latitude);
        wagonDevice.updateSpeed(speed);
        wagonDevice.updateMovedDistance(movedDistance);
        logger.info("--update wagon shadow --"+wagonDevice.toString());
        String payload = "{\"state\":{\"desired\":" + objectMapper.writeValueAsString(wagonDevice) + "}}";
        AWSIotMessage pub = new MessagePublisher(updateTopic,  AWSIotQos.QOS0, payload);
        return new ResponseEntity<String>(payload , HttpStatus.OK);
    }
    @RequestMapping(value = "/status/{status}" , method = RequestMethod.POST , produces = "application/json")
    public ResponseEntity<String> updateStatus(@PathVariable("status") String status){
        logger.debug("--delay--"+status);
        wagonDevice.updateStatus(status);
        return new ResponseEntity<String>(status , HttpStatus.OK);
    }
}
