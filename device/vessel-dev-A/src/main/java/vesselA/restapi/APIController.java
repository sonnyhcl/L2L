package vessel.restapi;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class APIController {
    private  static Logger logger = Logger.getLogger(APIController.class);
    @RequestMapping("/")
    String home() {
        logger.info("test rest api.");
        return "bazhu Hello **** World!";
    }
}
