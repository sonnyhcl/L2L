package supplierA.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import supplierA.domain.Logistics;

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

    public void arrange(String url , Logistics logistics){
        HttpEntity<Logistics> requestEntity = new HttpEntity<Logistics>(logistics, getHeaders());
        ResponseEntity<Logistics> response = restTemplate.postForEntity(url , requestEntity , Logistics.class);
        logger.info(response.getBody().toString());
    }
    public String matchManager(String url , HashMap<String , Object> payload) throws JsonProcessingException {
        logger.info("--match manager--");
        HttpEntity<?> requestEntity = new HttpEntity(payload, getHeaders());
        String rep = restTemplate.postForObject(url , requestEntity , String.class);
        logger.info(rep);
        return rep;
    }

    public HashMap<String , Object> queryLogistic(String url , HashMap<String , Object> payload){
        ParameterizedTypeReference<HashMap<String , Object>> responseType = new ParameterizedTypeReference<HashMap<String , Object>>(){};
        HttpEntity<HashMap<String , Object>> requestEntity = new HttpEntity<HashMap<String , Object>>(payload , getHeaders());
         ResponseEntity<HashMap<String, Object>> rep = restTemplate.exchange(url ,HttpMethod.POST , requestEntity ,responseType);
        logger.info(rep.toString());
        return rep.getBody();
    }
}
