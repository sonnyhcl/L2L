package slc.services;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import slc.domain.Logistics;
import slc.domain.LogisticsPart;

import java.util.Arrays;

@Service
@SuppressWarnings("all")
public class RestClient {
    private static Logger logger = LoggerFactory.getLogger(RestClient.class);
    @Autowired
    private RestTemplate restTemplate;

    private  HttpHeaders getHeaders(){
        String plainCredentials="admin:test";
        String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credentials);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }

    public String startLogistics(LogisticsPart logisticsPart, Logistics logistics){
        String url = logisticsPart.getUrl() + "/api/" + logisticsPart.getOrgId() + "/process-instances/MsgStartLogistic";
        HttpEntity<Logistics> requestEntity = new HttpEntity<Logistics>(logistics, getHeaders());
        String rep = restTemplate.postForObject(url , requestEntity , String.class);
        return rep;
    }
}
