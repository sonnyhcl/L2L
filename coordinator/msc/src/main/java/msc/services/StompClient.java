package msc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import msc.domain.SupplierPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SuppressWarnings("all")
public class StompClient {
    private static  final Logger logger = LoggerFactory.getLogger(StompClient.class);

    @Autowired
    public SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private RestClient restClient;
    @Autowired
    private ObjectMapper objectMapper;


    public void showInvlidRendezvousPorts(List<String> invalidDests){
        ObjectNode payload = objectMapper.createObjectNode();
        payload.putPOJO("invalidDestinations" , invalidDests);
        simpMessagingTemplate.convertAndSend("/topic/destinations/invalid", payload);
        logger.debug("send invalidDestinations "+invalidDests.toString());
    }

    public void showSupplier(SupplierPart supplierPart){
        ObjectNode payload = objectMapper.createObjectNode();
        payload.putPOJO("location" , restClient.getLoc(supplierPart));
        simpMessagingTemplate.convertAndSend("/topic/supplier/location", payload);
        logger.debug("send manager location "+supplierPart.getLocation());
    }
}
