package msc.services;

import msc.domain.Location;
import msc.domain.Order;
import msc.domain.SupplierPart;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    public String startSupplier(SupplierPart supplierPart, Order order){
        String url = supplierPart.getUrl() + "/api/" + supplierPart.getOrgId() + "/process-instances/MsgStartSupplier";
        HttpEntity<Order> requestEntity = new HttpEntity<Order>(order, getHeaders());
        String rep = restTemplate.postForObject(url , requestEntity , String.class);
        return rep;
    }

    public Location getLoc(SupplierPart  supplierPart){
        String url = "http://"+supplierPart.getHost()+":"+supplierPart.getPort()+"/api/location?name={name}";
        HttpEntity httpEntity = new HttpEntity(getHeaders());
        Map<String, Object> urlVariables = new HashMap<String, Object>();
        urlVariables.put("name",supplierPart.getLocation());
        ResponseEntity<Location> res = restTemplate.exchange(url , HttpMethod.GET , httpEntity , Location.class, urlVariables);
        return res.getBody();
    }
}
