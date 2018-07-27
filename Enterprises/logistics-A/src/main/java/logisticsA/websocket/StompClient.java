package logisticsA.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import logisticsA.domain.Rendezvous;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class StompClient {

    @Autowired
    public SimpMessagingTemplate simpMessagingTemplate;


    @Autowired
    private ObjectMapper objectMapper;

    public void sendPlanMissingMsg(String username , String topic , String pid , String policy ,   String reason ,  double C0 , double C1 , double  C2 ,  boolean  isFirst){
        ObjectNode payload = objectMapper.createObjectNode();
        payload = objectMapper.createObjectNode();
        payload.put("from" , pid);
        payload.put("policy" , policy);
        payload.put("reason" , reason);
        payload.put("C0" , C0);
        payload.put("C1" , C1);
        payload.put("C2" , C2);
        payload.put("isFirst" , isFirst);
        simpMessagingTemplate.convertAndSendToUser( username,topic , payload);
    }


    public void sendPlanFailMsg(String username , String topic , String pid , String policy ,  String msgType ,   String reason ,  double C0 , double C1 , double  C2 ,  boolean  isFirst){
        ObjectNode payload = objectMapper.createObjectNode();
        payload = objectMapper.createObjectNode();
        payload.put("from" , pid);
        payload.put("policy" , policy);
        payload.put("msgType", msgType);
        payload.put("reason" , reason);
        payload.put("C0" , C0);
        payload.put("C1" , C1);
        payload.put("C2" , C2);
        payload.put("isFirst" , isFirst);

        simpMessagingTemplate.convertAndSendToUser( username,topic , payload);
    }
    public void sendPlanSuccessMsg(String username , String topic , String pid , String policy , String msgType , String reason  , Rendezvous rend , double C0 , double C1 , double  C2 , boolean  isFirst){
        ObjectNode payload = objectMapper.createObjectNode();
        payload = objectMapper.createObjectNode();
        payload.put("from" , pid);
        payload.put("policy" , policy);
        payload.put("msgType", msgType);
        payload.put("reason" , reason);
        payload.put("C0" , C0);
        payload.put("C1" , C1);
        payload.put("C2" , C2);
        payload.put("isFirst" , isFirst);
        payload.putPOJO("rendezvous", rend);
        simpMessagingTemplate.convertAndSendToUser( username,topic , payload);
    }

    public void sendPlanFailMsg(String username , String topic , String pid , String policy ,   String reason ){
        ObjectNode payload = objectMapper.createObjectNode();
        payload = objectMapper.createObjectNode();
        payload.put("policy" , policy);
        payload.put("from" , pid);
        payload.put("reason" , reason);
        simpMessagingTemplate.convertAndSendToUser( username,topic , payload);
    }

    public void sendPlanSuccessMsg(String username , String topic , String pid , String policy ,   String reason  , Rendezvous rend,double totalCost , double riskCost){
        ObjectNode payload = objectMapper.createObjectNode();
        payload = objectMapper.createObjectNode();
        payload.put("policy" , policy);
        payload.put("from" , pid);
        payload.put("reason" , reason);
        payload.putPOJO("rendezvous", rend);
        payload.put("totalCost" ,  totalCost);
        payload.put("riskCost" , riskCost);
        simpMessagingTemplate.convertAndSendToUser( username,topic , payload);
    }
    public void sendFailSuccessMsg(String username , String topic , String pid , String policy ,   String reason ,double totalCost , double riskCost){
        ObjectNode payload = objectMapper.createObjectNode();
        payload = objectMapper.createObjectNode();
        payload.put("policy" , policy);
        payload.put("from" , pid);
        payload.put("reason" , reason);
        payload.put("totalCost" ,  totalCost);
        payload.put("riskCost" , riskCost);
        simpMessagingTemplate.convertAndSendToUser( username,topic , payload);
    }

    public void sendPlanMissingMsg(String username , String topic , String pid , String policy ,   String reason ,  double totalCost , double riskCost ){
        ObjectNode payload = objectMapper.createObjectNode();
        payload = objectMapper.createObjectNode();
        payload.put("policy" , policy);
        payload.put("from" , pid);
        payload.put("reason" , reason);
        payload.put("totalCost" ,  totalCost);
        payload.put("riskCost" , riskCost);
        simpMessagingTemplate.convertAndSendToUser( username,topic , payload);
    }
    public void sendCommand(String username , String topic , String cmd , String wid){
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("msgType", cmd);
        payload.put("from", wid);
        simpMessagingTemplate.convertAndSendToUser(username , topic, payload);
    }

}
