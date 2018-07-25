package slc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("all")
public class StompClient {
    private static  final Logger logger = LoggerFactory.getLogger(StompClient.class);

    @Autowired
    public SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private RestClient restClient;
    @Autowired
    private ObjectMapper objectMapper;
}
