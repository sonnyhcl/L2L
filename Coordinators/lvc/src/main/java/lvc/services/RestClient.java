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
import java.util.HashMap;
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
        String url = "http://"+vesselPart.getHost()+":"+vesselPart.getPort()+"/api/"+vpid+"/destinations/remain";
        ParameterizedTypeReference<List<Destination>> responseType = new ParameterizedTypeReference<List<Destination>>(){};
        HttpEntity httpEntity = new HttpEntity(getHeaders());
        ResponseEntity<List<Destination>> res = restTemplate.exchange(url , HttpMethod.GET , httpEntity , responseType);
        return  res.getBody();
    }

    public long getCurrentMs(VesselPart vesselPart, String vpid){
        String tUrl = "http://"+vesselPart.getHost()+":"+vesselPart.getPort()+"/api/"+vpid+"/currentTime";
        HttpEntity httpEntity = new HttpEntity(getHeaders());
        ResponseEntity<Long> currentMs = restTemplate.exchange(tUrl , HttpMethod.GET,httpEntity, Long.class);
        return currentMs.getBody();
    }

    public List<Freight> getFreights(LogisticsPart logisticPart){
        String url = "http://"+logisticPart.getHost()+":"+logisticPart.getPort()+"/api/freights";
        ParameterizedTypeReference<List<Freight>> responseType = new ParameterizedTypeReference<List<Freight>>(){};
        HttpEntity httpEntity = new HttpEntity(getHeaders());
        ResponseEntity<List<Freight>>  res = restTemplate.exchange(url , HttpMethod.GET , httpEntity , responseType);
        return  res.getBody();
    }

    public Logistics getLogistic(LogisticsPart logisticPart , String lpid){
        String url = "http://"+logisticPart.getHost()+":"+logisticPart.getPort()+"/api/"+lpid+"/logistics";
        HttpEntity httpEntity = new HttpEntity(getHeaders());
        ResponseEntity<Logistics> res = restTemplate.exchange(url , HttpMethod.GET , httpEntity , Logistics.class);
        return res.getBody();
    }

    public double getCurrentCost(LogisticsPart logisticPart , String lpid){
        String url = "http://"+logisticPart.getHost()+":"+logisticPart.getPort()+"/"+"/api/"+lpid+"/shadow/currentCost";
        HttpEntity httpEntity = new HttpEntity(getHeaders());
        ResponseEntity<Double> res = restTemplate.exchange(url , HttpMethod.GET , httpEntity , Double.class);
        return res.getBody();
    }

    public Logistics putLogistic(LogisticsPart logisticPart , String lpid , Logistics logistics){
        String url = "http://"+logisticPart.getHost()+":"+logisticPart.getPort()+"/"+"/api/"+lpid+"/logistics";
        HttpEntity httpEntity = new HttpEntity(logistics, getHeaders());
        ResponseEntity<Logistics> res = restTemplate.postForEntity(url , httpEntity  , Logistics.class);
        return res.getBody();
    }

    public  String postStatus(LogisticsPart logisticPart , String lpid , String status , HashMap<String , Object> msgBody){
        String url = "http://"+logisticPart.getHost()+":"+logisticPart.getPort()+"/api/"+lpid+"/shadow/status/"+status;
        HttpEntity<?> httpEntity = new HttpEntity(msgBody, getHeaders());
        ResponseEntity<String> res = restTemplate.postForEntity(url , httpEntity  , String.class);
        return res.getBody();
    }

    public  String postStatus(VesselPart vesselPart , String lpid , String status){
        String url = "http://"+vesselPart.getHost()+":"+vesselPart.getPort()+"/api/"+lpid+"/shadow/status/"+status;
        HttpEntity httpEntity = new HttpEntity(status, getHeaders());
        ResponseEntity<String> res = restTemplate.postForEntity(url , httpEntity  , String.class);
        return res.getBody();
    }

    public  String putRendezvous(VesselPart vesselPart , String vpid , String bestRend){
        String url = "http://"+vesselPart.getHost()+":"+vesselPart.getPort()+"/api/"+vpid+"/rendezvous/"+bestRend;
        HttpEntity httpEntity = new HttpEntity(bestRend, getHeaders());
        ResponseEntity<String> res = restTemplate.postForEntity(url , httpEntity  , String.class);
        return res.getBody();
    }


  public Application getApplication(VesselPart  vesselPart  , String vpid ){
        String url = "http://"+vesselPart.getHost()+":"+vesselPart.getPort()+"/api/"+vpid+"/application";
      HttpEntity httpEntity = new HttpEntity(getHeaders());
      ResponseEntity<Application> res = restTemplate.exchange(url , HttpMethod.GET , httpEntity , Application.class);
      return res.getBody();
  }

  public Destination getCurrentPort(VesselPart vesselPart , String vpid){
      String url = "http://"+vesselPart.getHost()+":"+vesselPart.getPort()+"/api/"+vpid+"/currentPort";
      HttpEntity httpEntity = new HttpEntity(getHeaders());
      ResponseEntity<Destination> res = restTemplate.exchange(url , HttpMethod.GET , httpEntity , Destination.class);
        return res.getBody();
  }
  public String getWagonStatus(LogisticsPart logisticsPart , String lpid){
      String url = "http://"+logisticsPart.getHost()+":"+logisticsPart.getPort()+"/api/"+lpid+"/wagon/status";
      HttpEntity httpEntity = new HttpEntity(getHeaders());
      ResponseEntity<String> res = restTemplate.exchange(url , HttpMethod.GET , httpEntity , String.class);
      return res.getBody();
  }
}
