package vesselA.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import vesselA.repos.VesselCache;

import java.security.Principal;

@Controller
public class StompMessageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private VesselCache vesselCache;

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
    @MessageMapping("/hello1")
    public void test1(String str , Principal principal) {
        System.out.println("***********/hello1**********" + " : " + str);
//        simpMessagingTemplate.convertAndSend("/topic/greetings" , "haha...");
        System.out.println(principal.getName());
        System.out.println("simpMessagingTemplate : " + simpMessagingTemplate);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/queue/greetings1", "haha...");
//        simpMessagingTemplate.convertAndSend("/queue/greetings", "haha...");
    }


}
