package soaring.l2l.coordinator.slc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import soaring.l2l.coordinator.slc.domain.SupplierPart;
import soaring.l2l.coordinator.slc.domain.Pair;
import soaring.l2l.coordinator.slc.domain.LogisticPart;
import soaring.l2l.coordinator.slc.repos.PairRepository;

import java.util.HashMap;

@Service
public class MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PairRepository pairLibraryService;




    /**
     * Send StartSupplier to manager-part for starting one manager process.
     * @param payload
     * @return
     * @throws JsonProcessingException
     */
    public String arrange(String fromOrgId , String fromPid , HashMap<String, Object> payload) throws JsonProcessingException {


    }

    public String queryLogistic(String fromOrgId , String fromPid , HashMap<String, Object> payload){

    }

    /**
     * *****************from Supplier participant****************************************
     * @param fromOrgId
     * @param fromPid
     * @param payload
     * @return
     * @throws JsonProcessingException
     */
    public String matchPartner(String fromOrgId , String fromPid , HashMap<String, Object> payload) throws JsonProcessingException {


    }




}
