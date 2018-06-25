package lvc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lvc.domain.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

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

    public List<Destination> getRemaingDestinations(VesselPart vesselPart , String vpid){
        String url = "http://"+vesselPart.getHost()+":"+vesselPart.getPort()+"/"+
                vesselPart.getProjectId()+"/api/"+vpid+"/destinations/remain";
        ParameterizedTypeReference<List<Destination>> responseType = new ParameterizedTypeReference<List<Destination>>(){};
        HttpEntity httpEntity = new HttpEntity(getHeaders());
        ResponseEntity<List<Destination>> res = restTemplate.exchange(url , HttpMethod.GET , httpEntity , responseType);
        return  res.getBody();
    }

    public long getCurrentMs(VesselPart vesselPart, String vpid){
        String tUrl = "http://"+vesselPart.getHost()+":"+vesselPart.getPort()+"/"+vesselPart.getProjectId()+"/api/"+vpid+"/currentTime";
        HttpEntity httpEntity = new HttpEntity(getHeaders());
        ResponseEntity<Long> currentMs = restTemplate.exchange(tUrl , HttpMethod.GET,httpEntity, Long.class);
        return currentMs.getBody();
    }

    public List<Freight> getFreights(LogisticPart logisticPart){
        String url = "http://"+logisticPart.getHost()+":"+logisticPart.getPort()+"/"+logisticPart.getProjectId()+"/api/freights";
        ParameterizedTypeReference<List<Freight>> responseType = new ParameterizedTypeReference<List<Freight>>(){};
        HttpEntity httpEntity = new HttpEntity(getHeaders());
        ResponseEntity<List<Freight>>  res = restTemplate.exchange(url , HttpMethod.GET , httpEntity , responseType);
        return  res.getBody();
    }

    public Logistic getLogistic(LogisticPart logisticPart , String lpid){
        String url = "http://"+logisticPart.getHost()+":"+logisticPart.getPort()+"/"+logisticPart.getProjectId()+"/api/"+lpid+"/logistic";
        HttpEntity httpEntity = new HttpEntity(getHeaders());
        ResponseEntity<Logistic> res = restTemplate.exchange(url , HttpMethod.GET , httpEntity , Logistic.class);
        return res.getBody();
    }


    public  Logistic putLogistic(LogisticPart logisticPart , String lpid , Logistic logistic){
        String url = "http://"+logisticPart.getHost()+":"+logisticPart.getPort()+"/"+logisticPart.getProjectId()+"/api/"+lpid+"/logistic";
        HttpEntity httpEntity = new HttpEntity(logistic , getHeaders());
        ResponseEntity<Logistic> res = restTemplate.postForEntity(url , httpEntity  , Logistic.class);
        return res.getBody();
    }

    public  String postStatus(LogisticPart logisticPart , String lpid , String status){
        String url = "http://"+logisticPart.getHost()+":"+logisticPart.getPort()+"/"+logisticPart.getProjectId()+"/api/"+lpid+"/shdow/"+status;
        HttpEntity httpEntity = new HttpEntity(status, getHeaders());
        ResponseEntity<String> res = restTemplate.postForEntity(url , httpEntity  , String.class);
        return res.getBody();
    }

    public  String postStatus(VesselPart vesselPart , String lpid , String status){
        String url = "http://"+vesselPart.getHost()+":"+vesselPart.getPort()+"/"+vesselPart.getProjectId()+"/api/"+lpid+"/shdow/"+status;
        HttpEntity httpEntity = new HttpEntity(status, getHeaders());
        ResponseEntity<String> res = restTemplate.postForEntity(url , httpEntity  , String.class);
        return res.getBody();
    }



}
