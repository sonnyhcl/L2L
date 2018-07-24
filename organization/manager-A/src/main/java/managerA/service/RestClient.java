package managerA.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import managerA.domain.Order;
import managerA.repos.CommonRepository;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
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
    @Autowired
    private CommonRepository commonRepository;

    private  HttpHeaders getHeaders(){
        String plainCredentials="admin:test";
        String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credentials);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }

    public String matchVessel(String url , HashMap<String , Object> payload) throws JsonProcessingException {
        logger.info("--match vessel--");
        HttpEntity<?> requestEntity = new HttpEntity(payload, getHeaders());
        String rep = restTemplate.postForObject(url , requestEntity , String.class);
        logger.info(rep);
        return rep;
    }

    public String  postOrder(String url , Order order){
        HttpEntity<Order> requestEntity = new HttpEntity<Order>(order, getHeaders());
        ResponseEntity<Order> response = restTemplate.postForEntity(url , requestEntity , Order.class);
        logger.info("Order is completed.");
        return response.getBody().toString();
    }

    public String test(){
//        HashMap<String , Object> sendData = new HashMap<String , Object>();
//        sendData.put("vOrgId" , "1");
//        sendData.put("vpid" ,2);
//        sendData.put("vid" ,2);
//        HttpEntity<?> requestEntity = new HttpEntity(sendData, getHeaders());
        String url = "http://10.131.245.91:9042/msc/hello";
        String rep = restTemplate.getForObject(url , String.class);
        return rep;
    }
}
