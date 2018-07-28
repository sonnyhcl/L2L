package iot.service.shadow;

import com.fasterxml.jackson.databind.ObjectMapper;
import iot.repos.PathRepository;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import iot.repos.CommonRepository;

@Data
@Service
public class MessageHandler {
    private static Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    @Autowired
    private PathRepository pathRepository;
    @Autowired
    private CommonRepository commonRepository;
    @Autowired
    private ObjectMapper objectMapper;

    public  void reportWagonState(){

    }

    
}
