package wagonA.services.shadow;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wagonA.domain.Path;
import wagonA.repos.CommonRepository;
import wagonA.repos.PathRepository;

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
