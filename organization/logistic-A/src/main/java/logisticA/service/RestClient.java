package logisticA.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import logisticA.domain.Logistic;
import logisticA.domain.Rendezvous;
import logisticA.domain.RoutePlan;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;

@Service
@SuppressWarnings("all")
public class RestClient {
    private static Logger logger = LoggerFactory.getLogger(RestClient.class);
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private  HttpHeaders getHeaders(){
        String plainCredentials="admin:test";
        String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credentials);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }

    public Rendezvous decide(String url , RoutePlan routePlan){
        HttpEntity<RoutePlan> requestEntity = new HttpEntity(routePlan, getHeaders());
        Rendezvous response = restTemplate.postForObject(url , requestEntity , Rendezvous.class);
        logger.info(response.toString());
        return response;
    }
    public String matchSupplier(String url , HashMap<String , Object> payload) {
        logger.info("--match supplier--");
        HttpEntity<?> requestEntity = new HttpEntity(payload, getHeaders());
        String rep = restTemplate.postForObject(url , requestEntity , String.class);
        logger.info(rep);
        return rep;
    }

    public String matchVessel(String url , HashMap<String , Object> payload) {
        logger.info("--match vessel--");
        HttpEntity<?> requestEntity = new HttpEntity(payload, getHeaders());
        String rep = restTemplate.postForObject(url , requestEntity , String.class);
        logger.info(rep);
        return rep;
    }


    public HashMap<String , Object> queryLogistic(String url){
        HashMap<String , Object> rep = restTemplate.getForObject(url ,HashMap.class);
        logger.info(rep.toString());
        return rep;
    }

    public String notifyMsg(String url){
        logger.info("--notifyArrival--");
        HttpEntity requestEntity = new HttpEntity(getHeaders());
        String rep = restTemplate.postForObject(url , requestEntity ,String.class);
        return  rep;
    }
}
