package iot.restapi;

import iot.domain.IoTClient;
import iot.service.AWSClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import iot.domain.Destination;
import iot.domain.VesselDevice;

import java.util.List;

@RestController
public class APIController {
    private static final Logger logger = LoggerFactory.getLogger(APIController.class);
    @Autowired
    private AWSClientService awsClientService;
    @RequestMapping("/hello")
    String home() {
        logger.info("test rest api.");
        return "hello , vessel-dev-A";
    }

    @RequestMapping(value = "vessel/{vid}/delay" , method = RequestMethod.POST , produces = "application/json")
    public ResponseEntity<List<Destination>> delay(@PathVariable("vid") String vid , @RequestBody  List<Destination> newDestinations){
        logger.debug("--delay--"+newDestinations.toString());
        IoTClient ioTClient = awsClientService.findDeviceClient(vid);
        VesselDevice vesselDevice = ioTClient.getVesselDevice();
        List<Destination> oldDestinations = vesselDevice.getDestinations();
        for(int i = 0 ; i < newDestinations.size();i++){
            Destination oldDest = oldDestinations.get(i);
            Destination newDest = newDestinations.get(i);
            oldDest.setEstiAnchorTime(newDest.getEstiAnchorTime());
            oldDest.setEstiArrivalTime(newDest.getEstiArrivalTime());
            oldDest.setEstiDepartureTime(newDest.getEstiDepartureTime());
        }
        return new ResponseEntity<List<Destination>>(newDestinations , HttpStatus.OK);
    }

    @RequestMapping(value = "vessel/{vid}/{status}" , method = RequestMethod.POST , produces = "application/json")
    public ResponseEntity<String> updateStatus(@PathVariable("vid") String vid , @PathVariable("status") String status){
        logger.debug("--delay--"+status);
        IoTClient ioTClient = awsClientService.findDeviceClient(vid);
        VesselDevice vesselDevice = ioTClient.getVesselDevice();
        vesselDevice.updateStatus(status);
        return new ResponseEntity<String>(status , HttpStatus.OK);
    }
}
