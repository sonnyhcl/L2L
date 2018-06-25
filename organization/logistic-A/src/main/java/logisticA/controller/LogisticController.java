package logisticA.controller;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import logisticA.awsiot.MessagePublisher;
import logisticA.domain.Freight;
import logisticA.domain.Location;
import logisticA.domain.Logistic;
import logisticA.domain.WagonShadow;
import logisticA.repos.*;
import logisticA.service.RestClient;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import sun.rmi.runtime.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@SuppressWarnings("all")
public class LogisticController extends AbstractController {
    private  final Logger logger = LoggerFactory.getLogger(LogisticController.class);

    @Autowired
    private RestClient restClient;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LogisticRepository logisticRepository;

    @Autowired
    private WagonShadowRepository wagonShadowRepository;

    @Autowired
    private FreightRepository freightRepository;

    @RequestMapping("/hello")
    public String hello(){
        logger.debug("hello ,logisticA");
        return "hello , logisticA";
    }

    /**
     * receive message StartLogistic from SLC.
     * @param orgId
     * @param msgType
     * @param payload
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/{orgId}/process-instances/MsgStartLogistic", method = RequestMethod.POST, produces = "application/json")
    public String startLogisticProc(@PathVariable("orgId") String orgId,
                                                    @RequestBody Logistic logistic) throws JsonProcessingException {
        logger.info("***********startLogisticProc***********");
        logger.info(orgId+" : MsgStartLogistic");
        //TODO: save to logistic repository
        logisticRepository.save(logistic);

        //TODO: start Logistic process instance by message MsgStartLogistic.
        Map<String , Object> vars = new HashMap<String , Object>();
        vars.put("orgId" , logistic.getLOrgId());
        vars.put("logisticId" , logistic.getId());
        vars.put("wid" , logistic.getWid());
        ProcessInstance pi = runtimeService.startProcessInstanceByMessage("MsgStartLogistic", vars);

        return "Start logistic successfully.";
    }

    @RequestMapping(value = "/{lpid}/logistic", method = RequestMethod.GET)
    public ResponseEntity<Logistic> queryLogisticByLPid(@PathVariable("lpid") String lpid) {
        logger.info("--GET /{pid}/logistic---"+lpid);
        Logistic result = null;
        result = logisticRepository.findByLpid(lpid);
        return new ResponseEntity<Logistic>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{pid}/logistic", method = RequestMethod.POST)
    public ResponseEntity<Logistic> updateLogistic(@PathVariable("pid") String pid , @RequestBody Logistic logistic) {
        logger.info("--POST /{pid}/logistic---"+pid);
        Logistic result = null;
        result = logisticRepository.update(logistic);
        return new ResponseEntity<Logistic>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/freights", method = RequestMethod.GET)
    public ResponseEntity<List<Freight>> queryVesselShadow() {
        logger.info("--GET /freights---");
        List<Freight> result = null;

        result = freightRepository.getFreights();

        return new ResponseEntity<List<Freight>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{pid}/shadow/{status}" , method = RequestMethod.POST , produces = "application/json")
    public ResponseEntity<String> updateStatus(@PathVariable("pid") String pid , @PathVariable("status") String status){
        logger.debug("--status--"+status);
        WagonShadow wagonShadow = wagonShadowRepository.findByPid(pid);
        wagonShadow.setStatus(status);
        return new ResponseEntity<String>(status , HttpStatus.OK);
    }
    @RequestMapping(value = "/{pid}/shadow" , method = RequestMethod.POST)
    ResponseEntity<String> updateShadow(@PathVariable("pid") String pid,@RequestBody HashMap<String , Object> mp) throws JsonProcessingException {
        String id = mp.get("id").toString();
        double longitude = Double.parseDouble(mp.get("longitude").toString());
        double latitude = Double.parseDouble(mp.get("latitude").toString());
        double speed = Double.parseDouble(mp.get("speed").toString());
        double movedDistance = Double.parseDouble(mp.get("movedDistance").toString());
        String status = mp.get("status").toString();

        WagonShadow wagonShadow = wagonShadowRepository.findByPid(pid);
        wagonShadow.setLongitude(longitude);
        wagonShadow.setLatitude(latitude);
        wagonShadow.setSpeed(speed);
        wagonShadow.setMovedDistance(movedDistance);
        wagonShadow.setStatus(status);

        return new ResponseEntity<String>(mp.toString() , HttpStatus.OK);
    }
    @RequestMapping(value = "/{pid}/shadow" , method = RequestMethod.GET)
    ResponseEntity<WagonShadow> getShadow(@PathVariable("pid") String pid ) throws JsonProcessingException {
        WagonShadow wagonShadow = wagonShadowRepository.findByPid(pid);
        return new ResponseEntity<WagonShadow>(wagonShadow , HttpStatus.OK);
    }

    @RequestMapping(value = "/{pid}/arrival" , method = RequestMethod.POST)
    ResponseEntity<WagonShadow> arrival(@PathVariable("pid") String pid ) throws JsonProcessingException {
        WagonShadow wagonShadow = wagonShadowRepository.findByPid(pid);
        wagonShadow.setStatus("Meeting");
        return new ResponseEntity<WagonShadow>(wagonShadow , HttpStatus.OK);
    }


}
