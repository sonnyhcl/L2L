package supplierpart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import supplierpart.cache.SupplierCache;
import supplierpart.controller.SupplierController;
import supplierpart.util.CommonUtil;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.HashMap;

@Service("arrangingService")
public class ArrangingService implements JavaDelegate ,Serializable{

    private  final Logger logger = LoggerFactory.getLogger(ArrangingService.class);


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SupplierCache supplierCache;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        //TODO: generate ordId for applying order
        String spid = delegateExecution.getProcessInstanceId();

        //TODO: Manually select a

        //TODO:获取流程数据
        HashMap<String, Object> pvars = (HashMap<String, Object>) runtimeService.getVariables(spid);
        String spName = pvars.get("spName").toString();
        int spNumber = Integer.parseInt(pvars.get("spNumber").toString());
        String orgId = pvars.get("orgId").toString();
        String logisticId = pvars.get("logisticId").toString();


        HashMap<String , Object> sendData = new HashMap<String , Object>();
        String deliveryNoteId = spid+CommonUtil.getGuid();
        sendData.put("deliveryNoteId" , deliveryNoteId);
        sendData.put("spName" , spName);
        sendData.put("spNumber" , spNumber);
        sendData.put("logisticId" , logisticId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String body = null;
        try {
            body = objectMapper.writeValueAsString(sendData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        String url = supplierCache.getCoBasePath()+"/supplier/"+orgId+"/"+spid+"/arrange";
        ResponseEntity<String> response = restTemplate.postForEntity(url , requestEntity , String.class);
        logger.info(response.getBody());

        logger.info("Generate order successfully :");
    }
}
