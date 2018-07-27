package logisticsA.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import logisticsA.domain.Path;
import logisticsA.domain.Rendezvous;
import logisticsA.repos.CommonRepository;
import logisticsA.repos.MapRepository;
import logisticsA.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.security.Principal;

@Controller
public class StompMessageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CommonRepository commonRepository;


    @Autowired
    private MapRepository mapRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public SimpMessagingTemplate simpMessagingTemplate;

    //Just for testing the communication of websocket
    @MessageMapping("/hello")
    public void test(String str) {
        System.out.println("***********/hello**********" + " : " + str);
//        simpMessagingTemplate.convertAndSend("/topic/greetings" , "haha...");
//        System.out.println(principal.getName());
        System.out.println("simpMessagingTemplate : " + simpMessagingTemplate);
        //simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/queue/greetings", "haha...");
        simpMessagingTemplate.convertAndSend("/queue/greetings", "haha...");
    }
    @MessageMapping("/testPath")
    public void test1(String str , Principal principal) throws IOException {
        System.out.println("***********/hello1**********" + " : " + str);
//        simpMessagingTemplate.convertAndSend("/topic/greetings" , "haha...");
        System.out.println(principal.getName());
        System.out.println("simpMessagingTemplate : " + simpMessagingTemplate);

        String pathInfo =mapRepository.PlanPath("100.340417", "27.376994"
                , "118.800095" , "32.146214");
        JsonNode pathNode = objectMapper.readTree(pathInfo);
        Path path = PathUtil.extractPath(pathNode);

        //TODO: send route info to navigator for displaying track and simulating running , navigator represents the device side.
        Rendezvous rd =  new Rendezvous();
        rd.setName("BeiJing");
        rd.setRoute(path);
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("msgType" , "RENDEZVOUS");
        payload.put("from" , "WA1234");
        payload.putPOJO("msgBody" , rd);
        simpMessagingTemplate.convertAndSendToUser( "admin","/topic/route" , payload);
    }


}
